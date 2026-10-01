plugins {
    id("ksuto.java-library")
}

group = "fr.ksuto"
version = "2.0"

dependencies {
    api(libs.guice)
    api(libs.ksuto.commons)

    constraints {
        // Guice 7 tire Guava 31, qui appelle sun.misc.Unsafe (avertissements sous Java 25)
        api(libs.guava)
    }
}
