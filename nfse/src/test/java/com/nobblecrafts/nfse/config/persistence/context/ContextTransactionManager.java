package com.nobblecrafts.nfse.config.persistence.context;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ContextTransactionManager {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    public void openTransaction() {
        if (em == null || !em.isOpen()) createEntityManager();
        log.debug("Opening transaction");
        if (!em.getTransaction().isActive()) em.getTransaction().begin();
    }

    public void closeTransaction() {
        if (em != null && em.isOpen()) em.close();
    }

    public void persistTransaction(List<?> entities) {
        log.debug("Persisting: {}", entities);
        try {
            for (Object entity : entities) {
                em.persist(entity);
            }
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            log.error("Transaction error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public EntityManager getEntityManager() {
        return em;
    }

    private void createEntityManager() {
        this.em = emf.createEntityManager();
    }
}

