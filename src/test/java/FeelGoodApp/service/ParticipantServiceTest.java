package FeelGoodApp.service;

import FeelGoodApp.domain.Event;
import FeelGoodApp.domain.MusicVenue;
import FeelGoodApp.domain.Participant;
import FeelGoodApp.domain.Performer;
import FeelGoodApp.domain.enums.GenreType;
import FeelGoodApp.domain.enums.PartnerLevel;
import FeelGoodApp.domain.enums.VenueType;
import FeelGoodApp.dto.ParticipantInfo;
import FeelGoodApp.dto.ParticipantInfoList;
import FeelGoodApp.dto.command.ParticipantCreateCommand;
import FeelGoodApp.exceptionhandling.EventIsNotAvailableForPublicException;
import FeelGoodApp.exceptionhandling.PastEventDateException;
import FeelGoodApp.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class ParticipantServiceTest {

    @Mock
    ParticipantRepository participantRepository;

    @Mock
    EventPerformerService eventPerformerService;

    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ParticipantService participantService;

    private Event firstEvent;
    private Participant firstParticipant;
    private Participant secondParticipant;

    @Test
    void testSaveParticipant_Success() {
        firstParticipant.setEvent(firstEvent);
        when(eventPerformerService.findEventById(1)).thenReturn(firstEvent);
        doNothing().when(eventPerformerService).checkEventDateIsPast(isA(Event.class));
        when(participantRepository.saveParticipant(firstParticipant)).thenReturn(firstParticipant);

        ParticipantInfo result = participantService.saveParticipant(1,
                new ParticipantCreateCommand("John Doe", "jd@gmail.com"));
        ParticipantInfo expected = modelMapper.map(firstParticipant, ParticipantInfo.class);

        assertEquals(expected, result);
    }

    @Test
    void testSaveParticipant_BecomeSoldOut() {
        firstParticipant.setEvent(firstEvent);
        when(eventPerformerService.findEventById(1)).thenReturn(firstEvent);
        doNothing().when(eventPerformerService).checkEventDateIsPast(isA(Event.class));
        when(participantRepository.saveParticipant(firstParticipant)).thenReturn(firstParticipant);

        ParticipantInfo result = participantService.saveParticipant(1,
                new ParticipantCreateCommand("John Doe", "jd@gmail.com"));

        assertThat(result.getEvent().getTicketCounter())
                .isEqualTo(4000);
        assertFalse(result.getEvent().isAvailableForPublic());
    }

    @Test
    void testSaveParticipant_EventIsNotAvailableForPublicException() {
        firstEvent.setAvailableForPublic(false);

        when(eventPerformerService.findEventById(1)).thenReturn(firstEvent);

        assertThrows(EventIsNotAvailableForPublicException.class, () ->
                participantService.saveParticipant(1, new ParticipantCreateCommand("Dohn Joe",
                        "dj@gmail.com")));
    }

    @Test
    void testSaveParticipant_PastEventDateException() {
        firstEvent.setId(1);
        firstEvent.setEventDate(LocalDateTime.now().minusDays(1));

        when(eventPerformerService.findEventById(1)).thenReturn(firstEvent);
        doThrow(new PastEventDateException(firstEvent.getId()))
                .when(eventPerformerService).checkEventDateIsPast(firstEvent);

        assertThrows(PastEventDateException.class, () ->
                participantService.saveParticipant(1, new ParticipantCreateCommand("Dohn Joe",
                        "dj@gmail.com")));
    }

    @Test
    void testGetParticipantsByEvent_EmptyList() {
        when(participantRepository.getParticipantsByEvent(1)).thenReturn(List.of());
        assertThat(participantService.getParticipantsByEvent(1)).isEmpty();
    }

    @Test
    void testGetParticipantsByEvent_AddOneParticipant() {
        when(participantRepository.getParticipantsByEvent(1)).thenReturn(List.of(firstParticipant));
        ParticipantInfoList firstParticipantInfoList = modelMapper.map(firstParticipant, ParticipantInfoList.class);

        List<ParticipantInfoList> result = participantService.getParticipantsByEvent(1);

        assertThat(result)
                .hasSize(1)
                .containsExactly(firstParticipantInfoList);
    }

    @Test
    void testGetMusicVenue_HasTwoMusicVenues() {
        when(participantRepository.getParticipantsByEvent(1)).thenReturn(List.of(firstParticipant, secondParticipant));
        ParticipantInfoList firstParticipantInfoList = modelMapper.map(firstParticipant, ParticipantInfoList.class);
        ParticipantInfoList secondParticipantInfoList = modelMapper.map(secondParticipant, ParticipantInfoList.class);

        List<ParticipantInfoList> result = participantService.getParticipantsByEvent(1);

        assertThat(result)
                .hasSize(2)
                .containsExactly(firstParticipantInfoList, secondParticipantInfoList);
    }

    @BeforeEach
    void init() {
        participantService = new ParticipantService(eventPerformerService, participantRepository, modelMapper);

        firstParticipant = new Participant();
        firstParticipant.setName("John Doe");
        firstParticipant.setEmail("jd@gmail.com");

        secondParticipant = new Participant();
        secondParticipant.setName("Dohn Joe");
        secondParticipant.setEmail("dj@gmail.com");

        Performer firstPerformer = new Performer();
        firstPerformer.setName("Képzelt Város");
        firstPerformer.setContactPhoneNumber("06-30-211-5555");
        firstPerformer.setEmail("kepzelt.varos@gmail.com");
        firstPerformer.setGenre(GenreType.POST_ROCK);
        firstPerformer.setPartnerLevel(PartnerLevel.TIER_1);
        firstPerformer.setDeleted(false);
        firstPerformer.setPerformersAtEvents(new ArrayList<>());
        firstPerformer.setDeletedAt(null);

        MusicVenue musicVenue = new MusicVenue();
        musicVenue.setName("Budapest Park");
        musicVenue.setContactPhoneNumber("06-30-211-3221");
        musicVenue.setAddress("Budapest, Soroksári út 60, 1095");
        musicVenue.setCapacity(4000);
        musicVenue.setType(VenueType.BANDSTAND);
        musicVenue.setDeleted(false);
        musicVenue.setDeletedAt(null);

        firstEvent = new Event();
        firstEvent.setEventName("Képzelt Város - Budapest Park");
        firstEvent.setEventDate(LocalDateTime.of(2022, Month.AUGUST, 18,
                18, 0, 0, 0));
        firstEvent.setAvailableForPublic(true);
        firstEvent.setEventPrice(3000.0);
        firstEvent.setTicketCounter(3999);
        firstEvent.setPerformersAtEvents(new ArrayList<>());
        firstEvent.setParticipants(new ArrayList<>());
        firstEvent.setDeleted(false);
        firstEvent.setDeletedAt(null);

        musicVenue.setEvents(List.of(firstEvent));
        firstEvent.setMusicVenue(musicVenue);
    }
}
