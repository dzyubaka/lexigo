plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.4-rc"
}

group = "ru.dzyubaka"
version = "0.4.1"

application {
    mainModule = "ru.dzyubaka.lexigo"
    mainClass = "ru.dzyubaka.lexigo.Main"
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    jpackage {
        imageName = "LexiGo! v0.4.1"
        appVersion = "0.4.1"
    }
}
