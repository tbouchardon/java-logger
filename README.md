# Logger

Journalisation commune aux **applications** ksuto (les bots), autour de SLF4J et Logback : sortie console au format
commun, fenêtre de log Swing optionnelle et journalisation automatique des appels de méthode (aspect Guice).

Les bibliothèques n'en dépendent pas : elles n'utilisent que `slf4j-api`.

Construit avec Gradle (conventions de `Bot Parent`) : `./gradlew build`.

## Utilisation

```kotlin
// settings.gradle.kts
includeBuild("../Commons")
includeBuild("../Logger")

// build.gradle.kts
dependencies { implementation(libs.ksuto.logger) }
```

Dans le code, l'API SLF4J habituelle :

```java
private static final Logger logger = LoggerFactory.getLogger(MaClasse.class);
```

## Console et fenêtre de log

L'application déclare un `src/main/resources/logback.xml` qui inclut les configurations fournies :

```xml
<configuration>
    <include resource="fr/ksuto/logger/logback-ksuto.xml"/>
    <!-- <include resource="fr/ksuto/logger/logback-ksuto-swing.xml"/> fenêtre de log, optionnelle -->
    <logger name="fr.ksuto.monbot" level="DEBUG"/>
    <root level="WARN"><appender-ref ref="CONSOLE"/></root>
</configuration>
```

| Ressource | Contenu |
|---|---|
| `logback-ksuto.xml` | Appender `CONSOLE` : heure, niveau, classe, méthode et ligne. Méthode et ligne lisent la pile d'appel : coûteux sur des logs très fréquents. |
| `logback-ksuto-swing.xml` | Appender `SWING` (`SwingAppender`), branché sur la racine : une fenêtre qui s'ouvre au premier log, en couleur selon le niveau, adaptée au thème clair ou sombre. Sans écran (WSL, serveur, tests), il ne fait rien. |

## Journalisation des appels (aspect Guice)

Les objets créés par Guice avec `LoggerModule` (`LoggerInjectors.getLoggerInjector()`) peuvent journaliser chacun de
leurs appels de méthode, avec ses arguments, indentés selon la profondeur d'appel. Désactivé par défaut ; se règle dans
un `logger.properties` de l'application :

| Propriété | Défaut | Sens |
|---|---|---|
| `ksuto.logger.aspect.enable` | `false` | `true` : active la journalisation des appels. |
| `ksuto.logger.aspect.packages` | `fr.ksuto:INFO` | Paquets surveillés et niveau du log, séparés par `;` (`fr.ksuto.monbot:DEBUG;fr.ksuto.prh:TRACE`). |
| `ksuto.logger.aspect.exclude.classes` | (vide) | Classes ignorées (noms complets), séparées par `\|`. |

## Projets qui s'en servent

| Projet | Ce qu'il utilise |
|---|---|
| ClockWork (bot World of Warcraft) | Console (fenêtre de log prévue, commentée dans son `logback.xml`) |
| Bot_Rumble (non maintenu) | `LoggerInjectors` et l'ancien `ConsoleLogger`, supprimé depuis : à remplacer par SLF4J |
