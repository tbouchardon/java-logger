package fr.ksuto.logger.tests;

import com.google.inject.Inject;

public class TestInjectedClass1 {
    
    @Inject
    Bot.TestNestedClass testNestedClass;
    @Inject
    InjectedClass injectedClass;
}
