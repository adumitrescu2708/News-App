package com.form.model.users;


import javax.persistence.*;

/**
 * Journalist Class
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */

@Entity
public class Journalist extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public Journalist() {

    }
}
