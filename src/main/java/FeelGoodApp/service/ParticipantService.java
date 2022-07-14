package FeelGoodApp.service;

import FeelGoodApp.domain.Event;
import FeelGoodApp.domain.Participant;
import FeelGoodApp.dto.ParticipantInfo;
import FeelGoodApp.dto.ParticipantInfoList;
import FeelGoodApp.dto.command.ParticipantCreateCommand;
import FeelGoodApp.exceptionhandling.EventIsNotAvailableForPublicException;
import FeelGoodApp.repository.ParticipantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParticipantService {

    private final EventPerformerService eventPerformerService;
    private final ParticipantRepository participantRepository;
    private final ModelMapper modelMapper;

    public ParticipantService(EventPerformerService eventPerformerService, ParticipantRepository participantRepository, ModelMapper modelMapper) {
        this.eventPerformerService = eventPerformerService;
        this.participantRepository = participantRepository;
        this.modelMapper = modelMapper;
    }

    public ParticipantInfo saveParticipant(Integer eventId, ParticipantCreateCommand command) {
        Event event = eventPerformerService.findEventById(eventId);
        eventPerformerService.checkEventDateIsPast(event);

        if (!event.isAvailableForPublic() || event.getTicketCounter() >= event.getMusicVenue().getCapacity()) {
            throw new EventIsNotAvailableForPublicException(eventId);
        }
        event.setTicketCounter(event.getTicketCounter() + 1);
        if (event.getTicketCounter() >= event.getMusicVenue().getCapacity()) {
            event.setAvailableForPublic(false);
        }
        Participant toSave = modelMapper.map(command, Participant.class);
        toSave.setEvent(event);
        Participant saved = participantRepository.saveParticipant(toSave);

        return modelMapper.map(saved, ParticipantInfo.class);
    }

    public List<ParticipantInfoList> getParticipantsByEvent(Integer eventId) {
        eventPerformerService.findEventById(eventId);
        List<Participant> participants = participantRepository.getParticipantsByEvent(eventId);
        return participants.stream()
                .map(participant -> modelMapper.map(participant, ParticipantInfoList.class))
                .collect(Collectors.toList());
    }
}
