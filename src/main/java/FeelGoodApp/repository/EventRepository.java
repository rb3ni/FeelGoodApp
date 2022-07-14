package FeelGoodApp.repository;

import FeelGoodApp.domain.Event;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Event saveEvent(Event toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public List<Event> findAllEvents() {
        return entityManager.createQuery("SELECT e " +
                "FROM Event e " +
                "WHERE e.deleted = false", Event.class).getResultList();
    }

    public Optional<Event> findEventById(Integer eventId) {
        return Optional.ofNullable(entityManager.find(Event.class, eventId));
    }

    public Event modifyEventDate(Event toUpdate) {
        Event updated = entityManager.merge(toUpdate);
        return updated;
    }

    public void deleteEvent(Event event) {
        event.setDeletedAt(LocalDateTime.now());
        event.setDeleted(true);
    }
}
