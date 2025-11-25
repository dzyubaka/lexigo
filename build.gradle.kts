plugins {
    id("org.beryx.jlink") version "3.1.4-rc"
}

application {
    mainClass = "ru.dzyubaka.lexigo.Main"
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    jpackage {
        imageName = "LexiGo! v0.3"
        appVersion = "0.3"
    }
}
