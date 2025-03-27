package fr.ksuto.logger;

import fr.ksuto.commons.PropertiesLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Singleton;

/**
 * Created by thomas.bouchardon on 20/03/2017!
 */
@Singleton
public class ConsoleLogger {
    
    public static int        DEBUG   = 1;
    public static int        ERROR   = 5;
    public static int        INFO    = 2;
    public static int        SUCCESS = 3;
    public static int        TRACE   = 0;
    public static int        WARN    = 4;
    private final Properties properties;
    int logLevel = 1;
    
    public ConsoleLogger() {
        
        this.properties = PropertiesLoader.load("logger");
        
        String logLevelProp = properties.getProperty("ksuto.logger.console.level", "WARN");
        
        switch (logLevelProp) {
            case "TRACE":
                logLevel = TRACE;
                break;
            case "DEBUG":
                logLevel = DEBUG;
                break;
            case "INFO":
                logLevel = INFO;
                break;
            case "SUCCESS":
                logLevel = SUCCESS;
                break;
            case "WARN":
                logLevel = WARN;
                break;
            case "ERROR":
                logLevel = ERROR;
                break;
            default:
                logLevel = 3;
        }
    }
    
    public void sysOut() {
        
        sysOut("");
    }
    
    public void sysOutDebug(String debug) {
        
        if (logLevel > DEBUG) {return;}
        sysOut("[DEBUG] ", debug, "");
    }
    
    public void sysOutError(String error) {
        
        if (logLevel > ERROR) {return;}
        sysOut("[ERROR] ", error, "");
    }
    
    public void sysOutInfo(String info) {
        
        if (logLevel > INFO) {return;}
        sysOut("[INFO] ", info, "");
    }
    
    public void sysOutSuccess(String success) {
        
        if (logLevel > SUCCESS) {return;}
        sysOut("[SUCCESS] ", success, "");
    }
    
    public void sysOutTrace(String trace) {
        
        if (logLevel > TRACE) {return;}
        sysOut("[TRACE] ", trace, "");
    }
    
    public void sysOutWarning(String warning) {
        
        if (logLevel > WARN) {return;}
        sysOut("[WARNING] ", warning, "");
    }
    
    private String extractRootPackage(String className, String regex) {
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(className);
        
        String rootPackage = "";
        
        if (matcher.find()) {
            rootPackage = matcher.group(0).substring(regex.replace("\\", "").length() - 4, matcher.group(0).length() - 1);
            rootPackage = String.valueOf(rootPackage.charAt(0)).toUpperCase() + rootPackage.substring(1);
        }
        
        return rootPackage;
    }
    
    private void sysOut(String prepend, String print, String append) {
        
        boolean erase = print.indexOf('\r') == 0;
        print = erase ? print.replace("\r", "") : print;
        
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        
        int stackNumber = 3;
        for (int i = 1, stackTraceElementsLength = stackTraceElements.length; i < stackTraceElementsLength; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (!stackTraceElement.getClassName().contains("ConsoleLogger")) {
                stackNumber = i;
                break;
            }
        }
        
        String className           = stackTraceElements[stackNumber].getClassName();
        String packageRootProperty = properties.getProperty("ksuto.logger.console.package.root", "");
        
        String rootPackage = "";
        if (!packageRootProperty.isEmpty()) {
            String packageRootRegex = packageRootProperty.replace(".", "\\.");
            rootPackage = extractRootPackage(className, packageRootRegex + "\\..*?\\.");
        }
        if (rootPackage.isEmpty()) {
            rootPackage = extractRootPackage(className, "fr\\.ksuto\\..*?\\.");
        }
        //
        
        Pattern pattern = Pattern.compile(".*\\.");
        Matcher matcher = pattern.matcher(className);
        String  classe  = className;
        if (matcher.find()) {
            classe = className.replace(matcher.group(0), "");
        }
        
        String method = stackTraceElements[stackNumber].getMethodName();
        
        int line = stackTraceElements[stackNumber].getLineNumber();
        
        int    stackSize       = stackTraceElements.length - stackNumber;
        String stackSizeSpaces = "";
        
        for (int i = 0; i < stackSize; i++) {stackSizeSpaces = stackSizeSpaces + " ";}
        
        if (erase) {
            System.out.print("\r" + new SimpleDateFormat("HH:mm:ss", Locale.FRANCE).format(new Date()) + " : " + stackSizeSpaces + rootPackage + " -> " + classe + " -> " + method + "() : (L" + line + ") " + prepend + print + append);
        }
        else {
            System.out.println("\r" + new SimpleDateFormat("HH:mm:ss", Locale.FRANCE).format(new Date()) + " : " + stackSizeSpaces + rootPackage + " -> " + classe + " -> " + method + "() : (L" + line + ")" +
                               " " + prepend + print + append);
        }
    }
    
    private void sysOut(String print) {
        
        sysOut("", print, "");
    }
}
