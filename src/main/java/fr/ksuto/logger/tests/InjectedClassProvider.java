package fr.ksuto.logger.tests;

import javax.inject.Provider;

public class InjectedClassProvider implements Provider<InjectedClass> {
    
    @Override
    public InjectedClass get() {
        
        return new InjectedClass();
    }
}
