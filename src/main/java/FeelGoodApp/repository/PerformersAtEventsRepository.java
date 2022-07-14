package FeelGoodApp.repository;

import FeelGoodApp.domain.PerformersAtEvents;
import FeelGoodApp.exceptionhandling.PerformerFromEventNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PerformersAtEventsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PerformersAtEvents savePerformerToEvent(PerformersAtEvents toSave) {
        toSave.setRegisteredAt(LocalDateTime.now());
        entityManager.persist(toSave);
        return toSave;
    }

    public PerformersAtEvents findByEventIdAndPerformerId(Integer eventId, Integer performerId) {
        try {
            return entityManager.createQuery("SELECT p " +
                            "FROM PerformersAtEvents p " +
                            "WHERE p.event.id = :eventParam AND p.performer.id = :performerParam", PerformersAtEvents.class)
                    .setParameter("eventParam", eventId)
                    .setParameter("performerParam", performerId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new PerformerFromEventNotFoundException(eventId, performerId);
        }
    }

    public void removePerformerFromEvent(PerformersAtEvents performersAtEvents) {
        entityManager.remove(performersAtEvents);
    }

    public List<PerformersAtEvents> removePerformerFromFutureEvents(Integer performerId) {
        return entityManager.createQuery("SELECT p " +
                        "FROM PerformersAtEvents p JOIN p.event e " +
                        "WHERE p.performer.id = :performerParam AND e.eventDate > :eventParam ", PerformersAtEvents.class)
                .setParameter("performerParam", performerId)
                .setParameter("eventParam", LocalDateTime.now())
                .getResultList();
    }
}
