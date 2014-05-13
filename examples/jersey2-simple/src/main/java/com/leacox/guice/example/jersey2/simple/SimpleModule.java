package com.leacox.guice.example.jersey2.simple;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

/**
 * @author John Leacox
 */
public class SimpleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SimpleResource.class);
    }

    @Provides
    @Singleton
    public String provideDisplay() {
        return "SimpleDisplay";
    }

    @Provides
    public SimpleService provideSimpleService(String display) {
        return new SimpleService(display);
    }
}
