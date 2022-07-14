package FeelGoodApp.repository;

import FeelGoodApp.domain.Participant;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ParticipantRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public Participant saveParticipant(Participant toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public List<Participant> getParticipantsByEvent(Integer eventId) {
        return entityManager.createQuery("SELECT p FROM Participant p JOIN p.event e " +
                        "WHERE e.id = :paramId AND e.deleted = false ", Participant.class)
                .setParameter("paramId", eventId)
                .getResultList();
    }
}
