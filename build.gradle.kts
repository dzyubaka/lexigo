plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.4-rc"
}

group = "ru.dzyubaka"
version = "1.0.2"

application {
    mainModule = "ru.dzyubaka.lexigo"
    mainClass = "ru.dzyubaka.lexigo.Main"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.dzyubaka.lexigo.Main"
    }
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    jpackage {
         imageName = "LexiGo! v1.0.2"
        appVersion = "1.0.2"
    }
}
