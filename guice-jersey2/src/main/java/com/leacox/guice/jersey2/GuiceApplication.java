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
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

/**
 * @author John Leacox
 */
public class GuiceApplication extends ResourceConfig {
    private static final String INJECTOR_NAME = Injector.class.getName();

    @Inject
    public GuiceApplication(ServiceLocator locator) {
        ServletContext servletContext = locator.getService(ServletContext.class);

        Injector injector = (Injector) servletContext.getAttribute(INJECTOR_NAME);
        injector.getInstance(Jersey2Module.ResourceConfigProvider.class).set(this);

        register(GuiceComponentProvider.class);
        registerBindings(injector);
    }

    private void registerBindings(Injector injector) {
        while (injector != null) {
            for (Key<?> key : injector.getBindings().keySet()) {
                Type type = key.getTypeLiteral().getType();
                if (type instanceof Class) {
                    Class<?> c = (Class) type;
                    if (c.isAnnotationPresent(Path.class)) {
                        register(c);
                    } else if (c.isAnnotationPresent(Provider.class)) {
                        register(c);
                    } else if (Feature.class.isAssignableFrom(c)) {
                        register(c);
                    }
                }
            }

            injector = injector.getParent();
        }
    }
}
