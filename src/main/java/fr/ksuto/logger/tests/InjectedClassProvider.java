package fr.ksuto.logger.tests;

import jakarta.inject.Provider;

public class InjectedClassProvider implements Provider<InjectedClass> {
    
    @Override
    public InjectedClass get() {
        
        return new InjectedClass();
    }
}
