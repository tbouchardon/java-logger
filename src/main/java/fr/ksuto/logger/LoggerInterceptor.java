package fr.ksuto.logger;

import java.util.StringJoiner;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Journalise chaque appel de méthode intercepté, indenté selon la profondeur d'appel, dans le logger de la classe appelée.
 */
public class LoggerInterceptor implements MethodInterceptor {

    private final Level                level;
    private final ThreadLocal<Integer> pile = ThreadLocal.withInitial(() -> 0);

    public LoggerInterceptor(String level) {

        this.level = Level.valueOf(level);
    }

    /**
     * @return le nom de la classe d'origine, sans le suffixe ajouté par Guice à la classe générée
     */
    private static String originalClassName(Object intercepted) {

        return intercepted.getClass().getName().replaceAll("\\$\\$.*", "");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        int depth = pile.get() + 1;
        pile.set(depth);

        try {
            String className = originalClassName(invocation.getThis());
            Logger logger    = LoggerFactory.getLogger(className);

            if (logger.isEnabledForLevel(level)) {

                StringBuilder sb = new StringBuilder();
                sb.append(depth);
                sb.append("  ".repeat(depth));
                sb.append(className.substring(className.lastIndexOf('.') + 1));
                sb.append(".");
                sb.append(invocation.getMethod().getName());
                sb.append("(");

                StringJoiner args = new StringJoiner(", ");

                for (Object o : invocation.getArguments()) {

                    if (o == null) {continue;}
                    if (o.toString().matches(".*\\..*@.*")) {args.add(o.toString().replaceAll(".*\\.(.*?)@.*", "$1"));}
                    else {args.add(o.toString());}
                }

                sb.append(args);
                sb.append(")");

                logger.atLevel(level).log(sb.toString());
            }

            return invocation.proceed();
        }
        finally {
            pile.set(depth - 1);
        }
    }
}
