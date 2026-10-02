package fr.ksuto.logger.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;

import java.awt.*;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Appender Logback qui affiche les logs dans une fenêtre Swing, en couleur selon le niveau.
 * <p>
 * La fenêtre n'est créée qu'au premier log reçu : déclarer l'appender sans le référencer n'ouvre rien.
 * Sans écran (WSL, serveur, tests), l'appender ne fait rien.
 */
public class SwingAppender extends AppenderBase<ILoggingEvent> {

    private static final Color TRACE_COLOR = new Color(180, 170, 160);
    private static final Color DEBUG_COLOR = new Color(120, 120, 120);
    private static final Color INFO_COLOR  = new Color(50, 50, 50);
    private static final Color WARN_COLOR  = new Color(238, 118, 33);
    private static final Color ERROR_COLOR = new Color(139, 26, 26);

    private Encoder<ILoggingEvent> encoder;
    private String                 title = "Logs";
    private boolean                headless;
    private JFrame                 frame;
    private JTextPane              textPane;

    @Override
    public void start() {

        if (encoder == null) {
            addError("Aucun encoder défini pour l'appender " + getName());
            return;
        }
        headless = GraphicsEnvironment.isHeadless();
        if (headless) {
            addWarn("Environnement sans écran : la fenêtre de log ne sera pas affichée");
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {

        if (headless) {return;}

        String text  = new String(encoder.encode(event), StandardCharsets.UTF_8);
        Color  color = colorOf(event.getLevel());

        SwingUtilities.invokeLater(() -> {
            if (frame == null) {createFrame();}
            write(text, color);
        });
    }

    private static Color colorOf(Level level) {

        return switch (level.toInt()) {
            case Level.ERROR_INT -> ERROR_COLOR;
            case Level.WARN_INT -> WARN_COLOR;
            case Level.INFO_INT -> INFO_COLOR;
            case Level.DEBUG_INT -> DEBUG_COLOR;
            default -> TRACE_COLOR;
        };
    }

    private void createFrame() {

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        java.net.URL icon = SwingAppender.class.getResource("/log.png");
        if (icon != null) {frame.setIconImage(Toolkit.getDefaultToolkit().createImage(icon));}

        textPane = new JTextPane();
        textPane.setEditable(false);
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);

        JScrollPane scrollPane = new JScrollPane(noWrapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(640, 300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void write(String text, Color color) {

        Style style = textPane.addStyle("level", null);
        StyleConstants.setForeground(style, color);
        Document document = textPane.getDocument();
        try {
            document.insertString(document.getLength(), text, style);
        }
        catch (BadLocationException e) {
            addError("Écriture impossible dans la fenêtre de log", e);
        }
        if (!frame.isVisible()) {frame.setVisible(true);}
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {

        this.encoder = encoder;
    }

    public void setTitle(String title) {

        this.title = title;
    }
}
