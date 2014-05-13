package com.leacox.guice.jersey2;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * @author John Leacox
 */
public class Jersey2Module extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    ResourceConfig provideResourceConfig(ResourceConfigProvider resourceConfigProvider) {
        return resourceConfigProvider.get();
    }

    @Singleton
    static class ResourceConfigProvider implements Provider<ResourceConfig> {
        private ResourceConfig config;

        @Inject
        ResourceConfigProvider() {
        }

        void set(ResourceConfig config) {
            this.config = config;
        }

        @Override
        public ResourceConfig get() {
            return config;
        }
    }
}