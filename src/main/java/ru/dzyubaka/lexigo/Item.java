package ru.dzyubaka.lexigo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private StringProperty russian;
    private StringProperty english;

    public Item() {
        setRussian("");
        setEnglish("");
    }

    public Item(String russian, String english) {
        setRussian(russian);
        setEnglish(english);
    }

    public String getRussian() {
        return russianProperty().get();
    }

    public void setRussian(String value) {
        russianProperty().set(value);
    }

    public StringProperty russianProperty() {
        if (russian == null) {
            russian = new SimpleStringProperty(this, "russian");
        }
        return russian;
    }
    public String getEnglish() {
        return englishProperty().get();
    }

    public void setEnglish(String value) {
        englishProperty().set(value);
    }

    public StringProperty englishProperty() {
        if (english == null) {
            english = new SimpleStringProperty(this, "english");
        }
        return english;
    }
}
