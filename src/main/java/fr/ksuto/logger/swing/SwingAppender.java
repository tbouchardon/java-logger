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
 * Sans écran (WSL, serveur, tests), l'appender ne fait rien. Les couleurs s'adaptent au thème Swing (clair ou sombre).
 */
public class SwingAppender extends AppenderBase<ILoggingEvent> {

    // Couleurs TRACE, DEBUG, WARN et ERROR pour un fond clair puis pour un fond sombre (l'INFO prend la couleur de texte du thème)
    private static final Color[] LIGHT_PALETTE = {new Color(160, 155, 150), new Color(110, 110, 110), new Color(205, 105, 20), new Color(175, 30, 30)};
    private static final Color[] DARK_PALETTE  = {new Color(115, 115, 115), new Color(160, 160, 160), new Color(235, 150, 60), new Color(240, 95, 95)};

    private Encoder<ILoggingEvent> encoder;
    private String                 title = "Logs";
    private boolean                headless;
    private JFrame                 frame;
    private JTextPane              textPane;

    /**
     * @return vrai si la couleur est sombre (luminance relative inférieure à 0,5)
     */
    static boolean isDark(Color color) {

        double luminance = (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255;
        return luminance < 0.5;
    }

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
        Level  level = event.getLevel();

        SwingUtilities.invokeLater(() -> {
            if (frame == null) {createFrame();}
            write(text, colorOf(level));
        });
    }

    private Color colorOf(Level level) {

        Color[] palette = isDark(textPane.getBackground()) ? DARK_PALETTE : LIGHT_PALETTE;

        return switch (level.toInt()) {
            case Level.ERROR_INT -> palette[3];
            case Level.WARN_INT -> palette[2];
            case Level.INFO_INT -> textPane.getForeground();
            case Level.DEBUG_INT -> palette[1];
            default -> palette[0];
        };
    }

    private void createFrame() {

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        java.net.URL icon = SwingAppender.class.getResource("/log.png");
        if (icon != null) {frame.setIconImage(Toolkit.getDefaultToolkit().createImage(icon));}

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
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
