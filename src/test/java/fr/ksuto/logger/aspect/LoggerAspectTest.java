package fr.ksuto.logger.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import fr.ksuto.logger.LoggerInjectors;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie l'aspect de log (LoggerModule / LoggerInterceptor), activé par src/test/resources/logger.properties.
 * L'interception génère du bytecode à l'exécution : c'est ce qui casse en premier lors d'une montée de Java ou de Guice.
 */
class LoggerAspectTest {

    private static final String SERVICE = "LoggerAspectTest$Service";

    private final Injector                   injector = LoggerInjectors.getLoggerInjector();
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final Logger                     root     = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    @BeforeEach
    void captureLogs() {

        appender.start();
        root.addAppender(appender);
    }

    @AfterEach
    void releaseLogs() {

        root.detachAppender(appender);
    }

    private List<String> messages() {

        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    @Test
    void logsNonPrivateMethodsOfInjectedInstances() {

        Service service = injector.getInstance(Service.class);

        service.publicMethod();
        service.protectedMethod();
        service.packagePrivateMethod();

        assertEquals(List.of("1  " + SERVICE + ".publicMethod()",
                             "1  " + SERVICE + ".protectedMethod()",
                             "1  " + SERVICE + ".packagePrivateMethod()"), messages());
    }

    @Test
    void logsAtConfiguredLevelInTheInterceptedClassLogger() {

        injector.getInstance(Service.class).publicMethod();

        ILoggingEvent event = appender.list.getFirst();
        assertEquals(Level.INFO, event.getLevel());
        assertEquals(Service.class.getName(), event.getLoggerName());
    }

    @Test
    void logsArgumentsAndIndentsNestedCalls() {

        injector.getInstance(Service.class).callsProtected("abc", 42);

        assertEquals(List.of("1  " + SERVICE + ".callsProtected(abc, 42)",
                             "2    " + SERVICE + ".protectedMethod()"), messages());
    }

    @Test
    void doesNotLogPrivateMethods() {

        injector.getInstance(Service.class).callsPrivate();

        assertEquals(List.of("1  " + SERVICE + ".callsPrivate()"), messages());
    }

    @Test
    void doesNotLogInstancesCreatedWithNew() {

        new Service().publicMethod();

        assertTrue(messages().isEmpty());
    }

    @Test
    void doesNotLogExcludedClasses() {

        injector.getInstance(NotLogged.class).publicMethod();

        assertTrue(messages().isEmpty());
    }

    @Test
    void injectsDependenciesOfInterceptedClasses() {

        Consumer consumer = injector.getInstance(Consumer.class);

        assertEquals(666, consumer.dependency.value);
        assertNotNull(consumer.service);
    }

    static class Service {

        public void publicMethod() {}

        protected void protectedMethod() {}

        void packagePrivateMethod() {}

        private void privateMethod() {}

        void callsProtected(String text, int number) {

            protectedMethod();
        }

        void callsPrivate() {

            privateMethod();
        }
    }

    static class NotLogged {

        public void publicMethod() {}
    }

    static class Dependency {

        int value = 666;
    }

    static class Consumer {

        @Inject
        Dependency dependency;

        @Inject
        Service service;
    }
}
