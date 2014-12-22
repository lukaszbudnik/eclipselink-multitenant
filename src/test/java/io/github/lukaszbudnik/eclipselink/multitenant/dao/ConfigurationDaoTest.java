package io.github.lukaszbudnik.eclipselink.multitenant.dao;


import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lukaszbudnik.eclipselink.multitenant.ioc.DaoGuiceModule;
import io.github.lukaszbudnik.eclipselink.multitenant.model.Configuration;
import io.github.lukaszbudnik.eclipselink.multitenant.model.WriteContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Optional;

public class ConfigurationDaoTest {

    private ConfigurationDao configurationDao;

    private WriteContext writeContext;
    private ReadContext readContext;

    @Before
    public void before() throws Exception {
        Injector i = Guice.createInjector(new DaoGuiceModule());
        configurationDao = i.getInstance(ConfigurationDao.class);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        writeContext = new WriteContext("a", keyPair.getPublic());
        readContext = new ReadContext("a", keyPair.getPrivate());
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void testSave() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setName("testSave() Configuration Name 1");
        configuration.setUsername("root");
        configuration.setPassword("secret");
        configurationDao.save(writeContext, configuration);

        Optional<Configuration> fetchedConfiguration = configurationDao.findByName(readContext, "testSave() Configuration Name 1");

        Assert.assertTrue(fetchedConfiguration.isPresent());
        Assert.assertArrayEquals(configuration.getKey(), fetchedConfiguration.get().getKey());
        Assert.assertEquals(configuration.getUsername(), fetchedConfiguration.get().getUsername());
        Assert.assertEquals(configuration.getPassword(), fetchedConfiguration.get().getPassword());
    }

}
