package io.github.lukaszbudnik.eclipselink.multitenant.dao;

import com.google.inject.Inject;
import io.github.lukaszbudnik.eclipselink.multitenant.model.Context;
import io.github.lukaszbudnik.eclipselink.multitenant.model.TestRun;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;


@Singleton
public class TestRunDao {

    @Inject
    EntityManagerFactory emf;


    public TestRun save(Context ctx, TestRun testRun) {
        EntityManager em = emf.createEntityManager();
        em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, ctx.getTenant());

        em.getTransaction().begin();
        TestRun newTestRun = em.merge(testRun);
        em.getTransaction().commit();
        return newTestRun;
    }

    public List<TestRun> findAll(Context ctx) {
        EntityManager em = emf.createEntityManager();
        em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, ctx.getTenant());

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TestRun> q = cb.createQuery(TestRun.class);
        Root<TestRun> c = q.from(TestRun.class);
        q.select(c);

        TypedQuery<TestRun> query = em.createQuery(q);
        List<TestRun> results = query.getResultList();

        return results;
    }

    public Optional<TestRun> findById(int id) {
        return Optional.empty();
    }

    public Optional<TestRun> findByName(String name) {
        return Optional.empty();
    }

    public void delete(TestRun testRun) {

    }

}
