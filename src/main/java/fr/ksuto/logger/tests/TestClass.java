package fr.ksuto.logger.tests;

public class TestClass {
    
    public void publicMethod() {
        
        System.out.println("-> public");
    }
    
    protected void protectedMethod() {
        
        System.out.println("-> protected");
    }
    
    void packagePrivateMethod() {
        
        System.out.println("-> packagePrivate");
    }
    
    private void privateMethod() {
        
        System.out.println("-> private");
    }
}
