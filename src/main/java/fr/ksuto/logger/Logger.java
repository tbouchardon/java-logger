package fr.ksuto.logger;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Created by Thomas on 11/10/2015!
 */
public class Logger {
    
    public static final String    NEXT           = "------------------------------------------------------------------------------------------------------------------";
    private final       Dimension dim_D          = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
    private final       int       i_SCREEN_WIDTH = (int) dim_D.getWidth();
    private final       String    copyright      = "\n                                                                                                            ©" +
                                                   " " +
                                                   "Thomas" +
                                                   " " +
                                                   "Bouchardon.";
    //	private final ShowsTrackerGUI showsTrackerGUI;
    private             JTextPane textPaneLog;
    private             Style     style;
    private             JFrame    frame;
    
    public Logger() {
        
        initGUI(false);
    }
    
    public Logger(boolean showLogger) {
        
        initGUI(showLogger);
    }
    
    public static void main(String[] args) {
        
        Logger logger = new Logger();
        logger.show();
        logger.log("Log : log");
        logger.logValid("Log : logValid");
        logger.logWarning("Log : logWarning");
        logger.logError("Log : logError");
        logger.logComment("Log : logComment");
    }
    
    public void log(String log) {
        
        StyleConstants.setForeground(style, new Color(50, 50, 50));
        logAll(log);
    }
    
    public void logComment(String log) {
        
        StyleConstants.setForeground(style, new Color(180, 170, 160));
        logAll(log);
    }
    
    public void logError(String log) {
        
        StyleConstants.setForeground(style, new Color(139, 26, 26));
        logAll(log);
    }
    
    public void logValid(String log) {
        
        StyleConstants.setForeground(style, new Color(113, 198, 113));
        logAll(log);
    }
    
    public void logWarning(String log) {
        
        StyleConstants.setForeground(style, new Color(238, 118, 33));
        logAll(log);
    }
    
    public void reduce() {
        
        frame.setState(Frame.ICONIFIED);
    }
    
    public void restore() {
        
        frame.setState(Frame.NORMAL);
    }
    
    public void show() {
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int       x          = (int) Math.floor(screenSize.getWidth() / 2 - (double) getFrame().getWidth() / 2);
        int       y          = (int) Math.floor(screenSize.getHeight() / 2 - (double) getFrame().getHeight() / 2);
        show(x, y);
    }
    
    public void show(int x, int y) {
        
        frame.setLocation(x, y);
        
        //		int x;
        //		x = showsTrackerGUI.frame.getX() + showsTrackerGUI.frame.getWidth() + 20;
        //		if (x + 490 > i_SCREEN_WIDTH) x = showsTrackerGUI.frame.getX() - 510;
        
        //		frame.setLocation(x, showsTrackerGUI.frame.getY());
        frame.setVisible(true);
    }
    
    public void showOrHide(int x, int y) {
        
        if (!getFrame().isVisible()) {
            show(x, y);
        }
        else { hide(); }
    }
    
    private void initGUI(boolean showLogger) {
        
        frame = new JFrame("net.ddns.ksuto.Logger");
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(490, 275));
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        frame.setResizable(false);
        frame.setBackground(Color.white);
        //		frame.setLocation(showsTrackerGUI.frame.getX() + showsTrackerGUI.frame.getWidth() + 15, showsTrackerGUI.frame.getY());
        
        java.net.URL url = ClassLoader.getSystemResource("log.png");
        Toolkit      kit = Toolkit.getDefaultToolkit();
        Image        img = kit.createImage(url);
        frame.setIconImage(img);
        
        JLabel labelConsole = new JLabel("  Log :");
        labelConsole.setPreferredSize(new Dimension(480, 20));
        
        textPaneLog = new JTextPane();
        DefaultCaret defaultCaret = (DefaultCaret) textPaneLog.getCaret();
        defaultCaret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
        
        style = textPaneLog.addStyle("Error Style", null);
        
        JPanel noWrapPanelLog = new JPanel(new BorderLayout());
        noWrapPanelLog.add(textPaneLog);
        
        JScrollPane scrollPaneLog = new JScrollPane(noWrapPanelLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants
                                                                                                                           .HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneLog.setPreferredSize(new Dimension(480, 222));
        scrollPaneLog.getVerticalScrollBar().setUnitIncrement(15);
        
        JButton buttonStop = new JButton("Stop");
        buttonStop.setPreferredSize(new Dimension(480, 20));
        
        frame.add(labelConsole);
        frame.add(scrollPaneLog);
        
        frame.pack();
        if (showLogger) { frame.setVisible(true); }
        
        StyleConstants.setForeground(style, new Color(230, 200, 230));
        Document doc = textPaneLog.getDocument();
        
        //		System.out.println("doc.getLength() : " + doc.getLength() + ", copyright.length() : " + copyright.length());
        try {
            doc.insertString(doc.getLength(), copyright, style);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void logAll(String log) {
        
        Document doc = textPaneLog.getDocument();
        
        try {
            if (doc.getLength() > copyright.length()) { doc.insertString(doc.getLength() - (copyright.length()), "\n", style); }
            doc.insertString(doc.getLength() - (copyright.length()), log, style);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void hide() {
        
        frame.setVisible(false);
    }
    
    private JFrame getFrame() {
        
        return frame;
    }
}
