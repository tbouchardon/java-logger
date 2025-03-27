package fr.ksuto.logger.tests;

import fr.ksuto.logger.LoggerInjectors;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class Bot {
    
    @Inject
    public TestClass testInjectedClass;
    
    @Inject
    public TestInjectedClass1 testInjectedClass1;
    
    public Bot() {}
    
    public static void main(String[] args) {
        
        Injector injector = LoggerInjectors.getLoggerInjector();
        Bot      Bot      = injector.getInstance(Bot.class);
        Bot.sout();
        
        TestClass testClass = new TestClass();
        
        System.out.println("---------- testClass ----------");
        testClass.publicMethod();
        //        testClass.packagePrivateMethod();
        //        testClass.protectedMethod();
        
        Bot.TestNestedClass testNestedClass = new Bot.TestNestedClass();
        
        System.out.println("---------- testNestedClass ----------");
        testNestedClass.publicMethod();
        testNestedClass.privateMethod();
        testNestedClass.packagePrivateMethod();
        testNestedClass.protectedMethod();
        
        System.out.println("---------- testInjectedClass ----------");
        Bot.testInjectedClass.publicMethod();
        Bot.testInjectedClass.packagePrivateMethod();
        Bot.testInjectedClass.protectedMethod();
        
        System.out.println("---------- testInjectedClass1 ----------");
        System.out.println(Bot.testInjectedClass1.injectedClass.evil);
        Bot.testInjectedClass1.testNestedClass.publicMethod();
        Bot.testInjectedClass1.testNestedClass.protectedMethod();
        Bot.testInjectedClass1.testNestedClass.packagePrivateMethod();
        Bot.testInjectedClass1.testNestedClass.privateMethod();
    }
    
    void sout() {
        
        System.out.println("Working!");
    }
    
    public static class TestNestedClass {
        
        public void publicMethod() {
            
            System.out.println("-> public");
//            publicMethod();
            protectedMethod();
            packagePrivateMethod();
            privateMethod();
        }
        
        protected void protectedMethod() {
            
            System.out.println("-> protected");
//            publicMethod();
//            protectedMethod();
//            packagePrivateMethod();
//            privateMethod();
        }
        
        void packagePrivateMethod() {
            
            System.out.println("-> packagePrivate");
//            publicMethod();
//            protectedMethod();
//            packagePrivateMethod();
//            privateMethod();
        }
        
        private void privateMethod() {
            
            System.out.println("-> private");
//            publicMethod();
//            protectedMethod();
//            packagePrivateMethod();
//            privateMethod();
        }
    }
}
