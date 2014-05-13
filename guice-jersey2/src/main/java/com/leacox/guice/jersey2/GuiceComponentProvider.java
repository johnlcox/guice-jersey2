/**
 * Copyright (C) 2014 John Leacox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leacox.guice.jersey2;

import com.google.inject.Injector;
import com.google.inject.Key;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * @author John Leacox
 */
public class GuiceComponentProvider implements ComponentProvider {
    private static final String INJECTOR_NAME = Injector.class.getName();

    private volatile ServiceLocator locator;
    private volatile Injector injector;

    public void initialize(ServiceLocator locator) {
        this.locator = locator;

        ServletContext servletContext = locator.getService(ServletContext.class);

        injector = (Injector) servletContext.getAttribute(INJECTOR_NAME);

        // ServiceLocatorUtilities.addOneConstant(locator, new DaggerInjectResolver(objectGraph));

        // Initialize HK2 guice-bridge
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }

    @Override
    public boolean bind(Class<?> component, Set<Class<?>> providerContracts) {
        if (injector == null) {
            return false;
        }

        if (injector.getBindings().keySet().contains(Key.get(component))) {
            DynamicConfiguration dynamicConfig = Injections.getConfiguration(locator);

            ServiceBindingBuilder bindingBuilder = Injections.newFactoryBinder(new GuiceComponentProvider
                    .GuiceManagedComponentFactory(injector, locator, component));
            bindingBuilder.to(component);
            Injections.addBinding(bindingBuilder, dynamicConfig);
            dynamicConfig.commit();

            return true;
        }

        return false;
    }

    @Override
    public void done() {
    }

    private static class GuiceManagedComponentFactory implements Factory {
        private final Injector injector;
        private final ServiceLocator locator;
        private final Class clazz;

        GuiceManagedComponentFactory(Injector injector, ServiceLocator locator, Class clazz) {
            this.injector = injector;
            this.locator = locator;
            this.clazz = clazz;
        }

        @Override
        public Object provide() {
            Object object = injector.getInstance(clazz);
            locator.inject(object);
            return object;
        }

        @Override
        public void dispose(Object instance) {
        }
    }
}
