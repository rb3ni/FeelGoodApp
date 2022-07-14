package FeelGoodApp.service;

import FeelGoodApp.domain.Event;
import FeelGoodApp.domain.MusicVenue;
import FeelGoodApp.domain.Performer;
import FeelGoodApp.domain.PerformersAtEvents;
import FeelGoodApp.domain.enums.GenreType;
import FeelGoodApp.domain.enums.PartnerLevel;
import FeelGoodApp.domain.enums.VenueType;
import FeelGoodApp.dto.EventInfo;
import FeelGoodApp.dto.EventInfoList;
import FeelGoodApp.dto.PerformerInfo;
import FeelGoodApp.dto.PerformerInfoList;
import FeelGoodApp.dto.command.*;
import FeelGoodApp.exceptionhandling.*;
import FeelGoodApp.repository.EventRepository;
import FeelGoodApp.repository.PerformerRepository;
import FeelGoodApp.repository.PerformersAtEventsRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class EventPerformerServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    PerformerRepository performerRepository;

    @Mock
    PerformersAtEventsRepository performersAtEventsRepository;

    @Mock
    MusicVenueService musicVenueService;

    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private EventPerformerService eventPerformerService;

    private Event firstEvent;
    private MusicVenue musicVenue;
    private Performer firstPerformer;
    private Performer secondPerformer;
    private PerformersAtEvents firstPerformerAtEvent;
    private PerformersAtEvents secondPerformerAtEvent;

    @Test
    void testSaveEvent_Success() {
        when(eventRepository.saveEvent(firstEvent)).thenReturn(firstEvent);
        when(musicVenueService.findMusicVenueById(1)).thenReturn(musicVenue);

        EventInfo result = eventPerformerService.saveEvent(new EventCreateCommand(
                LocalDateTime.of(2022, Month.AUGUST, 18, 18, 0, 0, 0),
                3999, 1));

        EventInfo expected = modelMapper.map(firstEvent, EventInfo.class);
        assertEquals(expected, result);
    }

    @Test
    void testSaveEvent_MusicVenueNotFoundException() {
        when(musicVenueService.findMusicVenueById(1)).thenThrow(MusicVenueNotFoundException.class);

        assertThrows(MusicVenueNotFoundException.class,
                () -> eventPerformerService.saveEvent(new EventCreateCommand(
                        LocalDateTime.of(2022, Month.AUGUST, 18, 18, 0, 0, 0),
                        12498, 1)));
    }

    @Test
    void testGetEvents_EmptyList() {
        when(eventRepository.findAllEvents()).thenReturn(List.of());
        assertThat(eventPerformerService.getEvents()).isEmpty();
    }

    @Test
    void testGetEvents_HasOneEvent() {
        when(eventRepository.findAllEvents()).thenReturn(List.of(firstEvent));
        EventInfo firstEventInfo = modelMapper.map(firstEvent, EventInfo.class);

        assertThat(eventPerformerService.getEvents())
                .hasSize(1)
                .containsExactly(firstEventInfo);
    }

    @Test
    void testGetEventById_Success() {
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));

        EventInfo result = eventPerformerService.getEventById(1);
        EventInfo expected = modelMapper.map(firstEvent, EventInfo.class);
        assertEquals(expected, result);
    }

    @Test
    void testGetEventById_EventNotFoundException() {
        when(eventRepository.findEventById(1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                eventPerformerService.getEventById(1));
    }

    @Test
    void testAddPerformerToEvent_Headliner_Tier_1_Success() {
        ReflectionTestUtils.setField(eventPerformerService, "tier_1", 3000.0);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));

        when(performersAtEventsRepository.savePerformerToEvent(firstPerformerAtEvent)).thenReturn(firstPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(1, true));

        firstEvent.setEventPrice(3000.0);
        firstEvent.setAvailableForPublic(true);
        firstEvent.setEventName("Képzelt Város - Budapest Park");

        EventInfo expected = modelMapper.map(firstEvent, EventInfo.class);
        expected.setPerformers(List.of(modelMapper.map(firstPerformer, PerformerInfoList.class)));

        assertEquals(expected, result);
    }

    @Test
    void testAddPerformerToEvent_Headliner_Tier_2_Success() {
        ReflectionTestUtils.setField(eventPerformerService, "tier_2", 5500.0);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_2);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        when(performersAtEventsRepository.savePerformerToEvent(firstPerformerAtEvent)).thenReturn(firstPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(1, true));

        assertThat(result.getEventPrice())
                .isEqualTo(5500.0);
    }

    @Test
    void testAddPerformerToEvent_Headliner_Tier_3_Success() {
        ReflectionTestUtils.setField(eventPerformerService, "tier_3", 7500.0);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_3);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        when(performersAtEventsRepository.savePerformerToEvent(firstPerformerAtEvent)).thenReturn(firstPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(1, true));

        assertThat(result.getEventPrice())
                .isEqualTo(7500.0);
    }

    @Test
    void testAddPerformerToEvent_Headliner_Tier_4_Success() {
        ReflectionTestUtils.setField(eventPerformerService, "tier_4", 9500.0);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_4);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        when(performersAtEventsRepository.savePerformerToEvent(firstPerformerAtEvent)).thenReturn(firstPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(1, true));

        assertThat(result.getEventPrice())
                .isEqualTo(9500.0);
    }

    @Test
    void testAddPerformerToEvent_Headliner_Tier_5_Success() {
        ReflectionTestUtils.setField(eventPerformerService, "tier_5", 13000.0);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_5);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        when(performersAtEventsRepository.savePerformerToEvent(firstPerformerAtEvent)).thenReturn(firstPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(1, true));

        assertThat(result.getEventPrice())
                .isEqualTo(13000.0);
    }

    @Test
    void testAddPerformerToEvent_NotHeadliner_Success() {
        secondPerformerAtEvent.setHeadliner(false);
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(2)).thenReturn(Optional.ofNullable(secondPerformer));

        when(performersAtEventsRepository.savePerformerToEvent(secondPerformerAtEvent)).thenReturn(secondPerformerAtEvent);
        EventInfo result = eventPerformerService.addPerformerToEvent(1,
                new AddPerformerToEventCommand(2, false));

        EventInfo expected = modelMapper.map(firstEvent, EventInfo.class);
        expected.setPerformers(List.of(modelMapper.map(secondPerformer, PerformerInfoList.class)));

        assertEquals(expected, result);
    }

    @Test
    void testAddPerformerToEvent_EventAlreadyHasHeadlinerException() {
        firstEvent.getPerformersAtEvents().add(firstPerformerAtEvent);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(2)).thenReturn(Optional.ofNullable(secondPerformer));

        assertThrows(EventAlreadyHasHeadlinerException.class, () ->
                eventPerformerService.addPerformerToEvent(1,
                        new AddPerformerToEventCommand(2, true)));
    }

    @Test
    void testAddPerformerToEvent_PastEventDateException() {
        firstEvent.setEventDate(LocalDateTime.now().minusDays(1));
        firstEvent.setId(1);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));

        assertThrows(PastEventDateException.class, () ->
                eventPerformerService.addPerformerToEvent(1,
                        new AddPerformerToEventCommand(1, true)));
    }

    @Test
    void testAddPerformerToEvent_HasSamePerformerException() {
        firstEvent.getPerformersAtEvents().add(firstPerformerAtEvent);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));

        assertThrows(HasSamePerformerException.class, () ->
                eventPerformerService.addPerformerToEvent(1,
                        new AddPerformerToEventCommand(1, false)));
    }

    @Test
    void testRemovePerformerFromEvent_NotHeadliner_Success() {
        firstEvent.getPerformersAtEvents().add(firstPerformerAtEvent);
        secondPerformerAtEvent.setHeadliner(false);
        firstEvent.getPerformersAtEvents().add(secondPerformerAtEvent);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performersAtEventsRepository.findByEventIdAndPerformerId(1, 2)).thenReturn(secondPerformerAtEvent);
        doNothing().when(performersAtEventsRepository).removePerformerFromEvent(isA(PerformersAtEvents.class));

        eventPerformerService.removePerformerFromEvent(1, 2);

        verify(performersAtEventsRepository, times(1)).findByEventIdAndPerformerId(1, 2);
        verify(performersAtEventsRepository, times(1)).removePerformerFromEvent(secondPerformerAtEvent);
    }

    @Test
    void testRemovePerformerFromEvent_PerformerFromEventNotFoundException() {
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performersAtEventsRepository.findByEventIdAndPerformerId(1, 2))
                .thenThrow(PerformerFromEventNotFoundException.class);

        assertThrows(PerformerFromEventNotFoundException.class, () ->
                eventPerformerService.removePerformerFromEvent(1, 2));
    }

    @Test
    void testRemovePerformerFromEvent_HeadlinerRemoveException() {
        firstEvent.getPerformersAtEvents().add(firstPerformerAtEvent);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performersAtEventsRepository.findByEventIdAndPerformerId(1, 1)).thenReturn(firstPerformerAtEvent);

        assertThrows(HeadlinerRemoveException.class, () ->
                eventPerformerService.removePerformerFromEvent(1, 1));
    }

    @Test
    void testRemovePerformerFromEvent_PastEventDateException() {
        firstEvent.setEventDate(LocalDateTime.now().minusDays(1));
        firstEvent.setId(1);

        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));

        assertThrows(PastEventDateException.class, () ->
                eventPerformerService.removePerformerFromEvent(1, 1));
    }

    @Test
    void testModifyEventDate_Success() {
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(eventRepository.modifyEventDate(firstEvent)).thenReturn(firstEvent);
        LocalDateTime newEventDate = LocalDateTime.of(2022, Month.AUGUST, 24, 18,
                0, 0, 0);
        EventInfo result = eventPerformerService.modifyEventDate(1,
                new EventDateUpdateCommand(newEventDate));

        assertThat(result.getEventDate())
                .isEqualTo(newEventDate);
    }

    @Test
    void testDeleteEvent_Success() {
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        eventPerformerService.deleteEvent(1);

        verify(eventRepository, times(1)).deleteEvent(firstEvent);
    }

    @Test
    void testSavePerformer_Success() {
        when(performerRepository.savePerformer(firstPerformer)).thenReturn(firstPerformer);
        PerformerInfo excepted = modelMapper.map(firstPerformer, PerformerInfo.class);

        PerformerInfo result = eventPerformerService.savePerformer(new PerformerCreateCommand("Képzelt Város",
                "kepzelt.varos@gmail.com", "06-30-211-5555",
                GenreType.POST_ROCK, PartnerLevel.TIER_1));

        assertEquals(excepted, result);
    }

    @Test
    void testSavePerformer_PerformerNameNotUniqueException_ConstraintViolationException() {
        when(performerRepository.savePerformer(firstPerformer)).thenThrow(ConstraintViolationException.class);

        assertThrows(PerformerNameNotUniqueException.class, () ->
                eventPerformerService.savePerformer(new PerformerCreateCommand("Képzelt Város",
                        "kepzelt.varos@gmail.com", "06-30-211-5555",
                        GenreType.POST_ROCK, PartnerLevel.TIER_1)));
    }

    @Test
    void testSavePerformer_PerformerNameNotUniqueException_DataIntegrityViolationException() {
        when(performerRepository.savePerformer(firstPerformer)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(PerformerNameNotUniqueException.class, () ->
                eventPerformerService.savePerformer(new PerformerCreateCommand("Képzelt Város",
                        "kepzelt.varos@gmail.com", "06-30-211-5555",
                        GenreType.POST_ROCK, PartnerLevel.TIER_1)));
    }

    @Test
    void testGetPerformers_EmptyList() {
        when(performerRepository.findAllPerformers()).thenReturn(List.of());
        assertThat(eventPerformerService.getPerformers()).isEmpty();
    }

    @Test
    void testGetPerformers_HasTwoPerformers() {
        when(performerRepository.findAllPerformers()).thenReturn(List.of(firstPerformer, secondPerformer));
        PerformerInfo firstPerformerInfo = modelMapper.map(firstPerformer, PerformerInfo.class);
        PerformerInfo secondPerformerInfo = modelMapper.map(secondPerformer, PerformerInfo.class);

        assertThat(eventPerformerService.getPerformers())
                .hasSize(2)
                .containsExactly(firstPerformerInfo, secondPerformerInfo);
    }

    @Test
    void testGetPerformerById_Success() {
        firstPerformer.getPerformersAtEvents().add(firstPerformerAtEvent);
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        PerformerInfo expected = modelMapper.map(firstPerformer, PerformerInfo.class);
        EventInfoList firstEventInfoList = modelMapper.map(firstEvent, EventInfoList.class);
        expected.setEvents(List.of(firstEventInfoList));

        PerformerInfo result = eventPerformerService.getPerformerById(1);
        assertEquals(expected, result);
    }

    @Test
    void testGetPerformerById_PerformerNotFoundException() {
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.empty());

        assertThrows(PerformerNotFoundException.class, () ->
                eventPerformerService.getPerformerById(1));
    }

    @Test
    void testModifyPerformerTier_Success() {
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        when(performerRepository.updatePerformerTier(firstPerformer)).thenReturn(firstPerformer);

        PerformerInfo result = eventPerformerService.modifyPerformerTier(1,
                new PerformerPartnerLevelUpdateCommand(PartnerLevel.TIER_4));

        assertThat(result.getPartnerLevel())
                .isEqualByComparingTo(PartnerLevel.TIER_4);
    }

    @Test
    void testDeletePerformer_PerformerHasNotAnyEvents() {
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        doNothing().when(performerRepository).deletePerformer(isA(Performer.class));
        when(performersAtEventsRepository.removePerformerFromFutureEvents(1)).thenReturn(List.of());

        eventPerformerService.deletePerformer(1);

        verify(performerRepository, times(1)).deletePerformer(firstPerformer);
        verify(performersAtEventsRepository, times(1)).removePerformerFromFutureEvents(1);
        verify(eventRepository, times(0)).deleteEvent(firstEvent);
        verify(performersAtEventsRepository, times(0)).removePerformerFromEvent(firstPerformerAtEvent);
    }

    @Test
    void testDeletePerformer_PerformerHasEvent_Headliner() {
        firstEvent.setId(1);
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        doNothing().when(performerRepository).deletePerformer(isA(Performer.class));
        when(performersAtEventsRepository.removePerformerFromFutureEvents(1))
                .thenReturn(List.of(firstPerformerAtEvent));
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        doNothing().when(eventRepository).deleteEvent(isA(Event.class));

        eventPerformerService.deletePerformer(1);

        verify(performerRepository, times(1)).deletePerformer(firstPerformer);
        verify(performersAtEventsRepository, times(1)).removePerformerFromFutureEvents(1);
        verify(eventRepository, times(1)).deleteEvent(firstEvent);
        verify(performersAtEventsRepository, times(0)).removePerformerFromEvent(firstPerformerAtEvent);
    }

    @Test
    void testDeletePerformer_PerformerHasEvent_NotHeadliner() {
        firstEvent.setId(1);
        firstPerformerAtEvent.setHeadliner(false);
        when(performerRepository.findPerformerById(1)).thenReturn(Optional.ofNullable(firstPerformer));
        doNothing().when(performerRepository).deletePerformer(isA(Performer.class));
        when(performersAtEventsRepository.removePerformerFromFutureEvents(1))
                .thenReturn(List.of(firstPerformerAtEvent));
        when(eventRepository.findEventById(1)).thenReturn(Optional.ofNullable(firstEvent));
        when(performersAtEventsRepository.findByEventIdAndPerformerId(1, 1))
                .thenReturn(firstPerformerAtEvent);
        doNothing().when(performersAtEventsRepository).removePerformerFromEvent(isA(PerformersAtEvents.class));

        eventPerformerService.deletePerformer(1);

        verify(performerRepository, times(1)).deletePerformer(firstPerformer);
        verify(performersAtEventsRepository, times(1)).removePerformerFromFutureEvents(1);
        verify(eventRepository, times(0)).deleteEvent(firstEvent);
        verify(performersAtEventsRepository, times(1)).removePerformerFromEvent(firstPerformerAtEvent);
    }

    @BeforeEach
    void init() {
        eventPerformerService = new EventPerformerService(musicVenueService, eventRepository, performerRepository,
                performersAtEventsRepository, modelMapper);

        musicVenue = new MusicVenue();
        musicVenue.setName("Budapest Park");
        musicVenue.setContactPhoneNumber("06-30-211-3221");
        musicVenue.setAddress("Budapest, Soroksári út 60, 1095");
        musicVenue.setCapacity(4000);
        musicVenue.setType(VenueType.BANDSTAND);
        musicVenue.setEvents(new ArrayList<>());
        musicVenue.setDeleted(false);
        musicVenue.setDeletedAt(null);

        firstPerformer = new Performer();
        firstPerformer.setName("Képzelt Város");
        firstPerformer.setContactPhoneNumber("06-30-211-5555");
        firstPerformer.setEmail("kepzelt.varos@gmail.com");
        firstPerformer.setGenre(GenreType.POST_ROCK);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_1);
        firstPerformer.setDeleted(false);
        firstPerformer.setPerformersAtEvents(new ArrayList<>());
        firstPerformer.setDeletedAt(null);

        secondPerformer = new Performer();
        secondPerformer.setName("Yara");
        secondPerformer.setContactPhoneNumber("06-30-211-5555");
        secondPerformer.setEmail("yara@gmail.com");
        secondPerformer.setGenre(GenreType.ROCK);
        secondPerformer.setPartnerLevel(PartnerLevel.TIER_3);
        secondPerformer.setDeleted(false);
        secondPerformer.setPerformersAtEvents(new ArrayList<>());
        secondPerformer.setDeletedAt(null);

        firstEvent = new Event();
        firstEvent.setEventName("No headliner performer yet - Budapest Park");
        firstEvent.setEventDate(LocalDateTime.of(2022, Month.AUGUST, 18,
                18, 0, 0, 0));
        firstEvent.setAvailableForPublic(false);
        firstEvent.setEventPrice(0.0);
        firstEvent.setTicketCounter(3999);
        firstEvent.setMusicVenue(musicVenue);
        firstEvent.setPerformersAtEvents(new ArrayList<>());
        firstEvent.setParticipants(new ArrayList<>());
        firstEvent.setDeleted(false);
        firstEvent.setDeletedAt(null);

        firstPerformerAtEvent = new PerformersAtEvents();
        firstPerformerAtEvent.setEvent(firstEvent);
        firstPerformerAtEvent.setPerformer(firstPerformer);
        firstPerformerAtEvent.setHeadliner(true);
        firstPerformerAtEvent.setRegisteredAt(null);

        secondPerformerAtEvent = new PerformersAtEvents();
        secondPerformerAtEvent.setEvent(firstEvent);
        secondPerformerAtEvent.setPerformer(secondPerformer);
        secondPerformerAtEvent.setHeadliner(true);
        secondPerformerAtEvent.setRegisteredAt(null);

    }
}
