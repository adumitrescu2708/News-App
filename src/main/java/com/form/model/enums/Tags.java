package com.form.model.enums;



/**
 * Type of News preferences in the platform
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
public enum Tags {
    HEALTH("health"),
    SPORT("sport"),
    POLITICS("politics"),
    ART("art"),
    CULINARY("culinary"),
    IT("it"),
    TRAVEL("travel"),
    EDUCATIONAL("educational"),
    MUSIC("music"),
    HOLLYWOOD("hollywood");

    private final String name;

    Tags(String name) {
        this.name = name;
    }
    public String getTag() {
        return name;
    }
}
