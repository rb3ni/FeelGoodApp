package FeelGoodApp.repository;

import FeelGoodApp.domain.Event;
import FeelGoodApp.domain.MusicVenue;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MusicVenueRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public MusicVenue saveMusicVenue(MusicVenue toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public Optional<MusicVenue> findMusicVenueById(Integer musicVenueId) {
        return Optional.ofNullable(entityManager.find(MusicVenue.class, musicVenueId));
    }

    public List<MusicVenue> findALlMusicVenues() {
        return entityManager.createQuery("SELECT m FROM MusicVenue m " +
                "WHERE m.deleted = false ", MusicVenue.class).getResultList();
    }

    public void deleteMusicVenue(MusicVenue musicVenue) {
        List<Event> futureEventsByMusicVenue = entityManager.createQuery("SELECT e FROM MusicVenue m " +
                        "JOIN m.events e " +
                        "WHERE m.id = :paramId AND e.deleted = false AND e.eventDate > :paramDate ", Event.class)
                .setParameter("paramId", musicVenue.getId())
                .setParameter("paramDate", LocalDateTime.now())
                .getResultList();

        musicVenue.setDeletedAt(LocalDateTime.now());
        musicVenue.setDeleted(true);
        for (Event event : futureEventsByMusicVenue) {
            event.setDeleted(true);
            event.setDeletedAt(LocalDateTime.now());
        }
    }
}
