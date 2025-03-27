package fr.ksuto.logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class LoggerInjectors {
    
    public static Injector getLoggerInjector() {
        
        return Guice.createInjector(new LoggerModule());
    }
}