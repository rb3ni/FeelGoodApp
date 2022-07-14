package FeelGoodApp.service;

import FeelGoodApp.domain.MusicVenue;
import FeelGoodApp.domain.enums.VenueType;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.command.MusicVenueCreateCommand;
import FeelGoodApp.exceptionhandling.MusicVenueNameNotUniqueException;
import FeelGoodApp.exceptionhandling.MusicVenueNotFoundException;
import FeelGoodApp.repository.MusicVenueRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class MusicVenueServiceTest {

    @Mock
    MusicVenueRepository musicVenueRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private MusicVenueService musicVenueService;

    private MusicVenue firstMusicVenue;
    private MusicVenue secondMusicVenue;

    @Test
    void testSaveMusicVenue_Success() {
        when(musicVenueRepository.saveMusicVenue(firstMusicVenue)).thenReturn(firstMusicVenue);

        MusicVenueInfo result = musicVenueService.saveMusicVenue(new MusicVenueCreateCommand("Budapest Park",
                "06-30-211-3221", "Budapest, Soroksári út 60, 1095",
                4000, VenueType.BANDSTAND));

        MusicVenueInfo expected = modelMapper.map(firstMusicVenue, MusicVenueInfo.class);
        assertEquals(expected, result);
    }

    @Test
    void testSaveMusicVenue_MusicVenueNameNotUniqueException_ConstraintViolationException() {
        when(musicVenueRepository.saveMusicVenue(firstMusicVenue)).thenThrow(ConstraintViolationException.class);

        assertThrows(MusicVenueNameNotUniqueException.class, () ->
                musicVenueService.saveMusicVenue(new MusicVenueCreateCommand("Budapest Park",
                        "06-30-211-3221", "Budapest, Soroksári út 60, 1095",
                        4000, VenueType.BANDSTAND)));
    }

    @Test
    void testSaveMusicVenue_MusicVenueNameNotUniqueException_DataIntegrityViolationException() {
        when(musicVenueRepository.saveMusicVenue(firstMusicVenue)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(MusicVenueNameNotUniqueException.class, () ->
                musicVenueService.saveMusicVenue(new MusicVenueCreateCommand("Budapest Park",
                        "06-30-211-3221", "Budapest, Soroksári út 60, 1095",
                        4000, VenueType.BANDSTAND)));
    }

    @Test
    void testGetMusicVenues_EmptyList() {
        when(musicVenueRepository.findALlMusicVenues()).thenReturn(List.of());
        assertThat(musicVenueService.getMusicVenues()).isEmpty();
    }

    @Test
    void testGetMusicVenues_HasOneMusicVenues() {
        when(musicVenueRepository.findALlMusicVenues()).thenReturn(List.of(firstMusicVenue));
        MusicVenueInfo firstMusicVenueInfo = modelMapper.map(firstMusicVenue, MusicVenueInfo.class);

        List<MusicVenueInfo> result = musicVenueService.getMusicVenues();

        assertThat(result)
                .hasSize(1)
                .containsExactly(firstMusicVenueInfo);
    }

    @Test
    void testGetMusicVenues_HasTwoMusicVenues() {
        when(musicVenueRepository.findALlMusicVenues()).thenReturn(List.of(firstMusicVenue, secondMusicVenue));
        MusicVenueInfo firstMusicVenueInfo = modelMapper.map(firstMusicVenue, MusicVenueInfo.class);
        MusicVenueInfo secondMusicVenueInfo = modelMapper.map(secondMusicVenue, MusicVenueInfo.class);

        List<MusicVenueInfo> result = musicVenueService.getMusicVenues();

        assertThat(result)
                .hasSize(2)
                .containsExactly(firstMusicVenueInfo, secondMusicVenueInfo);
    }

    @Test
    void testGetMusicVenueById_Success() {
        when(musicVenueRepository.findMusicVenueById(1)).thenReturn(Optional.ofNullable(firstMusicVenue));

        MusicVenueInfo expected = modelMapper.map(firstMusicVenue, MusicVenueInfo.class);
        MusicVenueInfo result = musicVenueService.getMusicVenueById(1);

        assertEquals(expected, result);
    }

    @Test
    void testGetMusicVenueById_MusicVenueNotFoundException() {
        when(musicVenueRepository.findMusicVenueById(1)).thenReturn(Optional.empty());

        assertThrows(MusicVenueNotFoundException.class, () ->
                musicVenueService.getMusicVenueById(1));
    }

    @Test
    void testGetMusicVenueById_SoftDeleted_MusicVenueNotFoundException() {
        firstMusicVenue.setDeleted(true);

        when(musicVenueRepository.findMusicVenueById(1)).thenReturn(Optional.empty());

        assertThrows(MusicVenueNotFoundException.class, () ->
                musicVenueService.getMusicVenueById(1));
    }

    @Test
    void testDeleteMusicVenue() {
        when(musicVenueRepository.findMusicVenueById(1)).thenReturn(Optional.ofNullable(firstMusicVenue));
        doNothing().when(musicVenueRepository).deleteMusicVenue(isA(MusicVenue.class));
        musicVenueService.deleteMusicVenue(1);

        verify(musicVenueRepository, times(1)).deleteMusicVenue(firstMusicVenue);
    }

    @Test
    void testDeleteMusicVenue_MusicVenueNotFoundException() {
        when(musicVenueRepository.findMusicVenueById(1)).thenReturn(Optional.empty());

        assertThrows(MusicVenueNotFoundException.class, () ->
                musicVenueService.deleteMusicVenue(1));
        verify(musicVenueRepository, times(1)).findMusicVenueById(1);
    }

    @BeforeEach
    void init() {
        musicVenueService = new MusicVenueService(musicVenueRepository, modelMapper);

        firstMusicVenue = new MusicVenue();
        firstMusicVenue.setName("Budapest Park");
        firstMusicVenue.setContactPhoneNumber("06-30-211-3221");
        firstMusicVenue.setAddress("Budapest, Soroksári út 60, 1095");
        firstMusicVenue.setCapacity(4000);
        firstMusicVenue.setType(VenueType.BANDSTAND);
        firstMusicVenue.setEvents(null);
        firstMusicVenue.setDeleted(false);
        firstMusicVenue.setDeletedAt(null);

        secondMusicVenue = new MusicVenue();
        secondMusicVenue.setName("Dürer kert");
        secondMusicVenue.setContactPhoneNumber("06-70-555-3221");
        secondMusicVenue.setAddress("Budapest, Öböl utca 1, 1117");
        secondMusicVenue.setCapacity(700);
        secondMusicVenue.setType(VenueType.CLUB);
        secondMusicVenue.setEvents(null);
        secondMusicVenue.setDeleted(false);
        secondMusicVenue.setDeletedAt(null);
    }
}
