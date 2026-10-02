package fr.ksuto.logger.aspect;

import fr.ksuto.logger.LoggerInjectors;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie l'aspect de log (LoggerModule / LoggerInterceptor), activé par src/test/resources/logger.properties.
 * L'interception génère du bytecode à l'exécution : c'est ce qui casse en premier lors d'une montée de Java ou de Guice.
 */
class LoggerAspectTest {

    private final Injector injector = LoggerInjectors.getLoggerInjector();

    private static String captureOutput(Runnable action) {

        PrintStream           original = System.out;
        ByteArrayOutputStream output   = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            action.run();
        }
        finally {
            System.setOut(original);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void logsNonPrivateMethodsOfInjectedInstances() {

        Service service = injector.getInstance(Service.class);

        String output = captureOutput(() -> {
            service.publicMethod();
            service.protectedMethod();
            service.packagePrivateMethod();
        });

        assertTrue(output.contains("[INFO] LoggerAspectTest$Service.publicMethod()"), output);
        assertTrue(output.contains("[INFO] LoggerAspectTest$Service.protectedMethod()"), output);
        assertTrue(output.contains("[INFO] LoggerAspectTest$Service.packagePrivateMethod()"), output);
    }

    @Test
    void logsArgumentsAndIndentsNestedCalls() {

        Service service = injector.getInstance(Service.class);

        String output = captureOutput(() -> service.callsProtected("abc", 42));

        assertTrue(output.contains("1  [INFO] LoggerAspectTest$Service.callsProtected(abc, 42)"), output);
        assertTrue(output.contains("2    [INFO] LoggerAspectTest$Service.protectedMethod()"), output);
    }

    @Test
    void doesNotLogPrivateMethods() {

        Service service = injector.getInstance(Service.class);

        String output = captureOutput(service::callsPrivate);

        assertTrue(output.contains("LoggerAspectTest$Service.callsPrivate()"), output);
        assertFalse(output.contains("LoggerAspectTest$Service.privateMethod()"), output);
    }

    @Test
    void doesNotLogInstancesCreatedWithNew() {

        String output = captureOutput(() -> new Service().publicMethod());

        assertEquals("", output);
    }

    @Test
    void doesNotLogExcludedClasses() {

        String output = captureOutput(() -> injector.getInstance(NotLogged.class).publicMethod());

        assertEquals("", output);
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
