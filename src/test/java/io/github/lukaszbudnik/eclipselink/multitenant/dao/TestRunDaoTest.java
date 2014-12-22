package io.github.lukaszbudnik.eclipselink.multitenant.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lukaszbudnik.eclipselink.multitenant.ioc.DaoGuiceModule;
import io.github.lukaszbudnik.eclipselink.multitenant.model.*;
import org.junit.*;

import javax.persistence.EntityManagerFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestRunDaoTest {

    private EntityManagerFactory emf;
    private TestRunDao testRunDao;
    private ConfigurationDao configurationDao;
    private StatusDao statusDao;

    private KeyPair keyPair;
    private WriteContext writeContextA;
    private ReadContext readContextA;

    @Before
    public void before() throws Exception {
        Injector i = Guice.createInjector(new DaoGuiceModule());
        testRunDao = i.getInstance(TestRunDao.class);
        configurationDao = i.getInstance(ConfigurationDao.class);
        statusDao = i.getInstance(StatusDao.class);
        emf = i.getInstance(EntityManagerFactory.class);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPair = keyPairGenerator.generateKeyPair();

        writeContextA = new WriteContext("a", keyPair.getPublic());
        readContextA = new ReadContext("a", keyPair.getPrivate());
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void testStatusFindByName() throws Exception {
        Optional<Status> created = statusDao.findByName(Status.Name.Created);
        Optional<Status> queued = statusDao.findByName(Status.Name.Queued);
        Optional<Status> executed = statusDao.findByName(Status.Name.Executed);
        Assert.assertTrue(created.isPresent());
        Assert.assertTrue(queued.isPresent());
        Assert.assertTrue(executed.isPresent());
    }

    @Test
    public void testSave() throws Exception {
        TestRun tra = new TestRun();
        tra.setName("testSave() Test Name A");
        tra = testRunDao.save(new Context("a"), tra);
        Assert.assertTrue(tra.getId() > 0);
    }

    @Test
    public void testSaveWithTestCases() throws Exception {
        TestRun tra = new TestRun();
        tra.setName("testSaveWithTestCases() Test Name A");

        TestCase tc1 = new TestCase();
        tc1.setTestRun(tra);
        tc1.setName("tc1 name");

        TestCase tc2 = new TestCase();
        tc1.setTestRun(tra);
        tc2.setName("tc2 name");

        List<TestCase> testCases = Arrays.asList(tc1, tc2);
        tra.setTestCases(testCases);

        tra = testRunDao.save(new Context("a"), tra);
        Assert.assertTrue(tra.getId() > 0);
        Assert.assertTrue(tra.getTestCases().stream().allMatch(tc -> tc.getId() > 0));
    }

    @Test
    @Ignore(value = "throws NPE from CMP3Policy.createPrimaryKeyFromId(CMP3Policy.java:224)")
    public void testCache() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setName("testCache() Configuration Name 1");
        configuration.setUsername("username");
        Configuration savedConfiguration = configurationDao.save(writeContextA, configuration);


        Assert.assertTrue(savedConfiguration.getId() > 0);
        Assert.assertTrue(emf.getCache().contains(Configuration.class, savedConfiguration.getId()));
    }

    @Test
    @Ignore(value = "setting FK on test run makes select from configuration table which requires private key...")
    public void testSaveWithConfiguration() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setName("testSaveWithConfiguration() Configuration Name 1");
        configuration.setUsername("root");
        configuration.setPassword("secret");
        Configuration savedConfiguration = configurationDao.save(writeContextA, configuration);

        Configuration fetchedConfiguration = configurationDao.findById(readContextA, savedConfiguration.getId());

        TestRun tra = new TestRun();
        tra.setName("testSaveWithConfiguration() Test Name A");
        tra.setConfiguration(fetchedConfiguration);
        tra = testRunDao.save(new Context("a"), tra);
        Assert.assertTrue(tra.getId() > 0);
        Assert.assertTrue(tra.getConfiguration().getId() > 0);
    }

    @Test
    public void testFindAll() throws Exception {
        TestRun trb = new TestRun();
        trb.setName("testFindAll() Test Name B");
        trb = testRunDao.save(new Context("b"), trb);
        Assert.assertTrue(trb.getId() == 1);

        TestRun tra = new TestRun();
        tra.setName("testFindAll() Test Name A1");
        tra = testRunDao.save(new Context("a"), tra);
        Assert.assertTrue(tra.getId() == 2);


        TestRun tra2 = new TestRun();
        tra2.setName("testFindAll() Test Name A2");
        tra2 = testRunDao.save(new Context("a"), tra2);
        Assert.assertTrue(tra2.getId() == 3);

        List<TestRun> allA = testRunDao.findAll(new Context("a"));
        Assert.assertEquals(2, allA.size());

        List<TestRun> allB = testRunDao.findAll(new Context("b"));
        Assert.assertEquals(1, allB.size());

        List<TestRun> none = testRunDao.findAll(new Context("c"));
        Assert.assertEquals(0, none.size());
    }

}
