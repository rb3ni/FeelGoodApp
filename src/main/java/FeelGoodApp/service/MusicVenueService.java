package FeelGoodApp.service;

import FeelGoodApp.domain.MusicVenue;
import FeelGoodApp.dto.MusicVenueInfo;
import FeelGoodApp.dto.command.MusicVenueCreateCommand;
import FeelGoodApp.exceptionhandling.MusicVenueNameNotUniqueException;
import FeelGoodApp.exceptionhandling.MusicVenueNotFoundException;
import FeelGoodApp.repository.MusicVenueRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MusicVenueService {

    private final MusicVenueRepository musicVenueRepository;
    private final ModelMapper modelMapper;

    public MusicVenueService(MusicVenueRepository musicVenueRepository, ModelMapper modelMapper) {
        this.musicVenueRepository = musicVenueRepository;
        this.modelMapper = modelMapper;
    }

    public MusicVenueInfo saveMusicVenue(MusicVenueCreateCommand command) {
        MusicVenue musicVenueToSave = modelMapper.map(command, MusicVenue.class);
        musicVenueToSave.setDeleted(false);
        try {
            MusicVenue musicVenueSaved = musicVenueRepository.saveMusicVenue(musicVenueToSave);
            return modelMapper.map(musicVenueSaved, MusicVenueInfo.class);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new MusicVenueNameNotUniqueException();
        }
    }

    public List<MusicVenueInfo> getMusicVenues() {
        List<MusicVenue> musicVenues = musicVenueRepository.findALlMusicVenues();
        return musicVenues.stream()
                .map(musicVenue -> modelMapper.map(musicVenue, MusicVenueInfo.class))
                .collect(Collectors.toList());
    }

    public MusicVenueInfo getMusicVenueById(Integer musicVenueId) {
        MusicVenue musicVenue = findMusicVenueById(musicVenueId);
        return modelMapper.map(musicVenue, MusicVenueInfo.class);
    }

    public void deleteMusicVenue(Integer musicVenueId) {
        MusicVenue musicVenueToDelete = findMusicVenueById(musicVenueId);
        musicVenueRepository.deleteMusicVenue(musicVenueToDelete);
    }

    protected MusicVenue findMusicVenueById(Integer musicVenueId) {
        Optional<MusicVenue> musicVenueFoundById = musicVenueRepository.findMusicVenueById(musicVenueId);
        if (musicVenueFoundById.isEmpty() || musicVenueFoundById.get().isDeleted()) {
            throw new MusicVenueNotFoundException(musicVenueId);
        }
        return musicVenueFoundById.get();
    }
}
