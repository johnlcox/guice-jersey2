package com.leacox.guice.example.jersey2.simple;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.leacox.guice.jersey2.Jersey2Module;

/**
 * @author John Leacox
 */
public class SimpleContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new SimpleModule(), new Jersey2Module());
    }
}
