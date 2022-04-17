package com.form.model.strategies;

import com.form.model.components.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable Pattern - used for dealing with updates in real time of users
 * timeline, depending on the state of the Posts Repository
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
public abstract class Observable {
    protected List<Observer> observers = new ArrayList<>();
    public abstract void attach(Observer observer);
    public abstract void notifyObservers(Post post);
}
