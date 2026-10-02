package fr.ksuto.logger.swing;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.status.Status;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie les configurations partagées fr/ksuto/logger/logback-ksuto*.xml, incluses comme dans le logback.xml d'une application.
 */
class LogbackKsutoConfigTest {

    private static final String CONSOLE = "<include resource=\"fr/ksuto/logger/logback-ksuto.xml\"/>";
    private static final String SWING   = "<include resource=\"fr/ksuto/logger/logback-ksuto-swing.xml\"/>";

    private static LoggerContext configure(String includes) throws Exception {

        String configuration = """
                <configuration>
                    %s
                    <root level="INFO"><appender-ref ref="CONSOLE"/></root>
                </configuration>
                """.formatted(includes);

        LoggerContext     context      = new LoggerContext();
        // Normalement fourni par SLF4J ; nécessaire pour logger via un contexte créé à la main
        context.setMDCAdapter(new LogbackMDCAdapter());
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(new ByteArrayInputStream(configuration.getBytes(StandardCharsets.UTF_8)));
        return context;
    }

    private static void assertNoStatusAbove(LoggerContext context, int level) {

        for (Status status : context.getStatusManager().getCopyOfStatusList()) {
            assertTrue(status.getLevel() < level, status.toString());
        }
    }

    @Test
    void consoleOnlyConfigurationIsSilent() throws Exception {

        LoggerContext context = configure(CONSOLE);
        Logger        root    = context.getLogger(Logger.ROOT_LOGGER_NAME);

        // Un avertissement ferait afficher à Logback tout son état interne à chaque démarrage
        assertNoStatusAbove(context, Status.WARN);
        assertInstanceOf(ConsoleAppender.class, root.getAppender("CONSOLE"));
        assertNull(root.getAppender("SWING"));
    }

    @Test
    void swingIncludeDeclaresAndAttachesTheWindow() throws Exception {

        LoggerContext context = configure(CONSOLE + SWING);
        Logger        root    = context.getLogger(Logger.ROOT_LOGGER_NAME);

        assertNoStatusAbove(context, Status.ERROR);
        assertInstanceOf(ConsoleAppender.class, root.getAppender("CONSOLE"));
        Appender<?> swing = root.getAppender("SWING");
        assertInstanceOf(SwingAppender.class, swing);
        assertTrue(swing.isStarted());
    }

    @Test
    void swingAppenderIgnoresLogsWithoutScreen() throws Exception {

        LoggerContext context = configure(CONSOLE + SWING);

        assertDoesNotThrow(() -> context.getLogger("test").info("sans écran, aucune fenêtre"));
        assertNoStatusAbove(context, Status.ERROR);
    }
}
