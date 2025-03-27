package fr.ksuto.logger;

import fr.ksuto.commons.PropertiesLoader;

import java.util.Arrays;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

public class LoggerModule extends AbstractModule {
    
    @Override
    protected void configure() {
        
        String   packageFilters;
        String[] classExclude;
        String   level;
        
        Properties properties = PropertiesLoader.load("logger");
        
        if (!properties.getProperty("ksuto.logger.aspect.enable", "false").equals("true")) {return;}
        packageFilters = properties.getProperty("ksuto.logger.aspect.packages", "fr.ksuto:INFO");
        classExclude = properties.getProperty("ksuto.logger.aspect.exclude.classes", "fr.ksuto.logger.ConsoleLogger|fr.ksuto.logger.Logger").split("\\|");
        
        for (String packageFilterAndLevel : packageFilters.split(";")) {
            
            String[] splitedPackageFilterAndLevel = packageFilterAndLevel.split(":");
            String   packageFilter                = splitedPackageFilterAndLevel[0];
            level = splitedPackageFilterAndLevel.length == 2 ? splitedPackageFilterAndLevel[1] : "TRACE";
            
            String[] finalclassExclude = classExclude;
            bindInterceptor(Matchers.inSubpackage(packageFilter).and(Matchers.not(new AbstractMatcher<Class>() {
                
                @Override
                public boolean matches(Class aClass) {
                    
                    return Arrays.asList(finalclassExclude).contains(aClass.getName());
                }
            })), Matchers.any(), new LoggerInterceptor(level));
        }
    }
}