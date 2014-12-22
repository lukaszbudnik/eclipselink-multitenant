package io.github.lukaszbudnik.eclipselink.multitenant.dao;

import io.github.lukaszbudnik.eclipselink.multitenant.encryption.AsymmetricEncryptionUtils;
import io.github.lukaszbudnik.eclipselink.multitenant.model.Configuration;
import io.github.lukaszbudnik.eclipselink.multitenant.model.WriteContext;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.security.SecureRandom;
import java.util.Optional;

@Singleton
public class ConfigurationDao {

    @Inject
    EntityManagerFactory emf;

    private SecureRandom secureRandom = new SecureRandom();

    public Configuration save(WriteContext ctx, Configuration configuration) throws Exception {
        EntityManager em = emf.createEntityManager();
        em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, ctx.getTenant());

        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        em.setProperty("key", key);

        byte[] encryptedKey = AsymmetricEncryptionUtils.encrypt(key, ctx.getPublicKey());
        configuration.setKey(encryptedKey);

        em.getTransaction().begin();
        Configuration newConfiguration = em.merge(configuration);
        em.getTransaction().commit();

        return newConfiguration;
    }

    public Optional<Configuration> findByName(ReadContext ctx, String name) {
        EntityManager em = emf.createEntityManager();
        em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, ctx.getTenant());
        em.setProperty("private-key", ctx.getPrivateKey());

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Configuration> q = cb.createQuery(Configuration.class);
        Root<Configuration> c = q.from(Configuration.class);
        ParameterExpression<String> p = cb.parameter(String.class);
        q.select(c).where(cb.equal(c.get("name"), p));

        TypedQuery<Configuration> query = em.createQuery(q);
        query.setParameter(p, name);

        Configuration configuration = query.getSingleResult();

        return Optional.ofNullable(configuration);
    }

    public Configuration findById(ReadContext ctx, int id) {
        EntityManager em = emf.createEntityManager();
        em.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, ctx.getTenant());
        em.setProperty("private-key", ctx.getPrivateKey());

        Configuration configuration = em.getReference(Configuration.class, id);

        return configuration;
    }
}
