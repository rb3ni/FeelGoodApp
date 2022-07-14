package FeelGoodApp.repository;

import FeelGoodApp.domain.Performer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PerformerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Performer> findPerformerById(Integer performerId) {
        return Optional.ofNullable(entityManager.find(Performer.class, performerId));
    }

    public Performer savePerformer(Performer toSave) {
        entityManager.persist(toSave);
        return toSave;
    }

    public List<Performer> findAllPerformers() {
        return entityManager.createQuery("SELECT p FROM Performer p " +
                "WHERE p.deleted = false ", Performer.class).getResultList();
    }

    public void deletePerformer(Performer performer) {
        performer.setDeletedAt(LocalDateTime.now());
        performer.setDeleted(true);
    }

    public Performer updatePerformerTier(Performer toUpdate) {
        Performer updated = entityManager.merge(toUpdate);
        return updated;
    }
}
