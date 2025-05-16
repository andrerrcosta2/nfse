package com.nobblecrafts.nfse.config;

import com.nobblecrafts.nfse.config.persistence.context.ContextTransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // JUnit 5
public abstract class AbstractContextualTest {

    @Autowired
    protected ContextTransactionManager tx;

    @BeforeEach
    void setUpTx() {
        tx.openTransaction();
    }

    @AfterEach
    void tearDownTx() {
        tx.closeTransaction();
    }

    protected <T> List<T> save(T... entities) {
        List<T> list = Arrays.asList(entities);
        tx.persistTransaction(list);
        return list;
    }

    protected <T> T getById(Class<T> entityClass, Object id) {
        return tx.getEntityManager().find(entityClass, id);
    }

    protected <T> void deleteById(Class<T> entityClass, Object id) {
        T entity = tx.getEntityManager().find(entityClass, id);
        if (entity != null) {
            tx.getEntityManager().remove(entity);
        }
    }

    protected <T, ID> void deleteAllByIds(Class<T> entityClass, List<ID> ids) {
        EntityManager em = tx.getEntityManager();
        em.createQuery("DELETE FROM " + entityClass.getSimpleName() + " e WHERE e.id IN :ids")
                .setParameter("ids", ids)
                .executeUpdate();
        em.clear();
    }
}
