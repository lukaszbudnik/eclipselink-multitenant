package io.github.lukaszbudnik.eclipselink.multitenant.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.onami.lifecycle.jsr250.PostConstructModule;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class DaoGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        this.install(new PostConstructModule());
    }

    @Provides
    @Singleton
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory("jpa-eclipselink");
    }

}
