package com.form.model.enums;

/**
 * Type of Contribution Types in the platform
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
public enum Role {
    JOUNRALIST("Journalist"),
    READER("Reader");
    private final String name;

    Role(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
