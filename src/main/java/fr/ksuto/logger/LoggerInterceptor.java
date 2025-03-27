package fr.ksuto.logger;

import java.util.StringJoiner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LoggerInterceptor implements MethodInterceptor {
    
    private final String level;
    private       int    pile = 0;
    
    public LoggerInterceptor(String level) {
        
        this.level = level;
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
        try {
            pile++;
            
            StringBuilder sb = new StringBuilder();
            sb.append(pile);
            for (int i = 0; i < pile; i++) { sb.append("  "); }
            
            sb.append("[").append(level).append("] ");
            sb.append(invocation.getThis().getClass().getName().replaceAll(".*\\.(.*?)\\$\\$.*", "$1"));
            sb.append(".");
            sb.append(invocation.getMethod().getName());
            sb.append("(");
            
            StringJoiner args = new StringJoiner(", ");
            
            for (Object o : invocation.getArguments()) {
                
                if (o == null) { continue; }
                if (o.toString().matches(".*\\..*@.*")) { args.add(o.toString().replaceAll(".*\\.(.*?)@.*", "$1")); }
                else { args.add(o.toString()); }
            }
            
            sb.append(args.toString());
            
            sb.append(")");
            
            System.out.println(sb.toString());
            
            return invocation.proceed();
        }
        finally {
            pile--;
        }
    }
}
