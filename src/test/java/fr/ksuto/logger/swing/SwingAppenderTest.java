package fr.ksuto.logger.swing;

import java.awt.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwingAppenderTest {

    @Test
    void detectsDarkBackgrounds() {

        assertTrue(SwingAppender.isDark(Color.BLACK));
        assertTrue(SwingAppender.isDark(new Color(60, 63, 65)), "fond Darcula / FlatDark");
        assertFalse(SwingAppender.isDark(Color.WHITE));
        assertFalse(SwingAppender.isDark(new Color(242, 242, 242)), "fond FlatLight");
    }
}
