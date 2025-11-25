package ru.dzyubaka.lexigo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private StringProperty original;
    private StringProperty translation;

    public Item() {

    }

    public Item(String original, String translation) {
        setOriginal(original);
        setTranslation(translation);
    }

    public String getOriginal() {
        return originalProperty().get();
    }

    public void setOriginal(String value) {
        originalProperty().set(value);
    }

    public StringProperty originalProperty() {
        if (original == null) {
            original = new SimpleStringProperty(this, "original");
        }
        return original;
    }
    public String getTranslation() {
        return translationProperty().get();
    }

    public void setTranslation(String value) {
        translationProperty().set(value);
    }

    public StringProperty translationProperty() {
        if (translation == null) {
            translation = new SimpleStringProperty(this, "translation");
        }
        return translation;
    }
}
