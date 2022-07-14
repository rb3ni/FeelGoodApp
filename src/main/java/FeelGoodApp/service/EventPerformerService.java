package FeelGoodApp.service;

import FeelGoodApp.domain.Event;
import FeelGoodApp.domain.MusicVenue;
import FeelGoodApp.domain.Performer;
import FeelGoodApp.domain.PerformersAtEvents;
import FeelGoodApp.domain.enums.PartnerLevel;
import FeelGoodApp.dto.*;
import FeelGoodApp.dto.command.*;
import FeelGoodApp.exceptionhandling.*;
import FeelGoodApp.repository.EventRepository;
import FeelGoodApp.repository.PerformerRepository;
import FeelGoodApp.repository.PerformersAtEventsRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventPerformerService {

    @Value("${event.price.tier_1}")
    private Double tier_1;
    @Value("${event.price.tier_2}")
    private Double tier_2;
    @Value("${event.price.tier_3}")
    private Double tier_3;
    @Value("${event.price.tier_4}")
    private Double tier_4;
    @Value("${event.price.tier_5}")
    private Double tier_5;

    private final MusicVenueService musicVenueService;
    private final EventRepository eventRepository;
    private final PerformerRepository performerRepository;
    private final PerformersAtEventsRepository performersAtEventsRepository;
    private final ModelMapper modelMapper;

    public EventPerformerService(MusicVenueService musicVenueService, EventRepository eventRepository, PerformerRepository performerRepository, PerformersAtEventsRepository performersAtEventsRepository, ModelMapper modelMapper) {
        this.musicVenueService = musicVenueService;
        this.eventRepository = eventRepository;
        this.performerRepository = performerRepository;
        this.performersAtEventsRepository = performersAtEventsRepository;
        this.modelMapper = modelMapper;
    }

    public EventInfo saveEvent(EventCreateCommand command) {
        MusicVenue musicVenue = musicVenueService.findMusicVenueById(command.getMusicVenueId());

        Event eventToSave = new Event();
        eventToSave.setEventDate(command.getEventDate());
        eventToSave.setTicketCounter(command.getTicketCounter());
        eventToSave.setAvailableForPublic(false);
        eventToSave.setEventPrice(0.0);
        eventToSave.setDeleted(false);
        eventToSave.setParticipants(new ArrayList<>());
        eventToSave.setPerformersAtEvents(new ArrayList<>());
        eventToSave.setEventName("No headliner performer yet - " + musicVenue.getName());
        eventToSave.setMusicVenue(musicVenue);

        Event eventSaved = eventRepository.saveEvent(eventToSave);
        return mapEventToEventInfo(eventSaved);
    }

    public List<EventInfo> getEvents() {
        List<Event> events = eventRepository.findAllEvents();
        return events.stream()
                .map(this::mapEventToEventInfo)
                .collect(Collectors.toList());
    }

    public EventInfo getEventById(Integer eventId) {
        Event event = findEventById(eventId);
        return mapEventToEventInfo(event);
    }

    public EventInfo addPerformerToEvent(Integer eventId, AddPerformerToEventCommand command) {
        Performer performerToAdd = findPerformerById(command.getPerformerId());
        Event event = findEventById(eventId);
        checkEventDateIsPast(event);

        if (event.getPerformersAtEvents().stream()
                .map(PerformersAtEvents::getPerformer).collect(Collectors.toList()).contains(performerToAdd)) {
            throw new HasSamePerformerException(command.getPerformerId());
        }
        if (command.getIsHeadliner()) {
            if (eventHasNotHeadliner(event)) {
                event.setEventName(performerToAdd.getName() + " - " + event.getMusicVenue().getName());
                modifyEventPrice(performerToAdd.getPartnerLevel(), event);
                event.setAvailableForPublic(true);
            } else {
                throw new EventAlreadyHasHeadlinerException(eventId);
            }
        }

        PerformersAtEvents toSave = new PerformersAtEvents();
        toSave.setEvent(event);
        toSave.setPerformer(performerToAdd);
        toSave.setHeadliner(command.getIsHeadliner());
        PerformersAtEvents saved = performersAtEventsRepository.savePerformerToEvent(toSave);
        event.getPerformersAtEvents().add(saved);
        return mapEventToEventInfo(event);
    }

    public void removePerformerFromEvent(Integer eventId, Integer performerId) {
        Event event = findEventById(eventId);
        checkEventDateIsPast(event);

        PerformersAtEvents performersAtEventsFound =
                performersAtEventsRepository.findByEventIdAndPerformerId(eventId, performerId);
        if (performersAtEventsFound.isHeadliner()) {
            throw new HeadlinerRemoveException(eventId, performerId);
        }
        performersAtEventsRepository.removePerformerFromEvent(performersAtEventsFound);
    }

    public EventInfo modifyEventDate(Integer eventId, EventDateUpdateCommand command) {
        Event eventToUpdate = findEventById(eventId);
        checkEventDateIsPast(eventToUpdate);

        eventToUpdate.setEventDate(command.getEventDate());
        Event eventUpdated = eventRepository.modifyEventDate(eventToUpdate);
        return mapEventToEventInfo(eventUpdated);
    }

    public void deleteEvent(Integer eventId) {
        Event eventToDelete = findEventById(eventId);
        eventRepository.deleteEvent(eventToDelete);
    }

    public PerformerInfo savePerformer(PerformerCreateCommand command) {
        Performer toSave = modelMapper.map(command, Performer.class);
        toSave.setDeleted(false);
        toSave.setPerformersAtEvents(new ArrayList<>());
        try {
            Performer saved = performerRepository.savePerformer(toSave);
            return mapPerformerToPerformerInfo(saved);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new PerformerNameNotUniqueException();
        }
    }

    public List<PerformerInfo> getPerformers() {
        List<Performer> performers = performerRepository.findAllPerformers();
        return performers.stream()
                .map(this::mapPerformerToPerformerInfo)
                .collect(Collectors.toList());
    }

    public PerformerInfo getPerformerById(Integer performerId) {
        Performer performer = findPerformerById(performerId);
        return mapPerformerToPerformerInfo(performer);
    }

    public PerformerInfo modifyPerformerTier(Integer performerId, PerformerPartnerLevelUpdateCommand command) {
        Performer performerToUpdate = findPerformerById(performerId);

        performerToUpdate.setPartnerLevel(command.getPartnerLevel());
        Performer updated = performerRepository.updatePerformerTier(performerToUpdate);
        return mapPerformerToPerformerInfo(updated);
    }

    public void deletePerformer(Integer performerId) {
        Performer performerToDelete = findPerformerById(performerId);
        performerRepository.deletePerformer(performerToDelete);

        List<PerformersAtEvents> performersAtEvents =
                performersAtEventsRepository.removePerformerFromFutureEvents(performerId);
        for (PerformersAtEvents performersAtEvent : performersAtEvents) {
            if (performersAtEvent.isHeadliner()) {
                deleteEvent(performersAtEvent.getEvent().getId());
            } else {
                removePerformerFromEvent(performersAtEvent.getEvent().getId(), performerId);
            }
        }
    }

    private void modifyEventPrice(PartnerLevel partnerLevel, Event event) {
        switch (partnerLevel) {
            case TIER_1:
                event.setEventPrice(tier_1);
                break;
            case TIER_2:
                event.setEventPrice(tier_2);
                break;
            case TIER_3:
                event.setEventPrice(tier_3);
                break;
            case TIER_4:
                event.setEventPrice(tier_4);
                break;
            case TIER_5:
                event.setEventPrice(tier_5);
                break;
        }
    }

    private PerformerInfo mapPerformerToPerformerInfo(Performer performer) {
        PerformerInfo performerInfo = modelMapper.map(performer, PerformerInfo.class);
        List<EventInfoList> eventInfoList = performer.getPerformersAtEvents().stream()
                .map(PerformersAtEvents::getEvent)
                .filter(event -> !event.isDeleted())
                .map(event -> modelMapper.map(event, EventInfoList.class))
                .collect(Collectors.toList());

        performerInfo.setEvents(eventInfoList);
        return performerInfo;
    }

    private EventInfo mapEventToEventInfo(Event event) {
        EventInfo eventInfo = modelMapper.map(event, EventInfo.class);
        List<PerformerInfoList> performerInfoList = event.getPerformersAtEvents().stream()
                .map(PerformersAtEvents::getPerformer)
                .map(performer -> modelMapper.map(performer, PerformerInfoList.class))
                .collect(Collectors.toList());
        List<ParticipantInfoList> participantInfoList = event.getParticipants().stream()
                .map(participant -> modelMapper.map(participant, ParticipantInfoList.class))
                .collect(Collectors.toList());

        eventInfo.setPerformers(performerInfoList);
        eventInfo.setParticipants(participantInfoList);
        return eventInfo;
    }

    private boolean eventHasNotHeadliner(Event event) {
        return event.getPerformersAtEvents().stream()
                .filter(PerformersAtEvents::isHeadliner)
                .map(PerformersAtEvents::isHeadliner)
                .findFirst()
                .isEmpty();
    }

    private Performer findPerformerById(Integer performerId) {
        Optional<Performer> performerFound = performerRepository.findPerformerById(performerId);
        if (performerFound.isEmpty() || performerFound.get().isDeleted()) {
            throw new PerformerNotFoundException(performerId);
        }
        return performerFound.get();
    }

    protected void checkEventDateIsPast(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new PastEventDateException(event.getId());
        }
    }

    protected Event findEventById(Integer eventId) {
        Optional<Event> eventFound = eventRepository.findEventById(eventId);
        if (eventFound.isEmpty() || eventFound.get().isDeleted()) {
            throw new EventNotFoundException(eventId);
        }
        return eventFound.get();
    }
}
