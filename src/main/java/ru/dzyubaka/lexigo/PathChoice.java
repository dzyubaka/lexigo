package ru.dzyubaka.lexigo;

import java.nio.file.Path;

public record PathChoice(Path path) {
    @Override
    public String toString() {
        var fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
