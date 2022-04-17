package com.form.model.users;

import com.form.model.enums.Tags;
import com.form.model.enums.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * User
 * <p>
 *     Default object class for user, will be extended by Journalist class and
 *     Reader class. Stores the required data.
 * </p>
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
@Entity
@Table(name = "users_table")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {
    /** User id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /** Username */
    @Column
    private String username;
    /** Password */
    @Column
    private String password;
    /** Role */
    @Column
    private Role role;
    /** List of all preferences */
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Tags> tags = new ArrayList<>();


    /** Checks for a tag in the preferences */
    public boolean hasTag(Tags tag) {
        return tags.contains(tag);
    }

    /** hashCode and Equals methos */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    /** Basic getters and setters for all fields */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
