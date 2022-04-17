package com.form.services;

import com.form.model.components.Timeline;
import com.form.model.users.Journalist;
import com.form.model.users.Reader;
import com.form.model.users.User;
import com.form.repositories.UserRepository;
import com.form.model.enums.Role;
import com.form.model.enums.Tags;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User's Service
 *
 * <p>
 *     MVC Service Class that handles operations on Users repository,
 *     such as Log In request, filtering requests and retrieve request.
 * </p>
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Component
@Service
public class UserService {
    /** Users Repository - Constructor Injected */
    private final UserRepository userRepository;

    /** Constructor */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Register Request
     * <p>
     *     This method is used for saving a new User Object to the repository.
     *     Firstly, it checks the existence of the dates in the database and then
     *     creates a new Reader / Journalist Entity. In case of a new Reader,
     *     a new Timeline is assigned.
     * </p>
     *
     * @param username - new user's username
     * @param tags - new user's tags
     * @param password - new user's password
     * @param role - Journalist/ Reader
     * */
    public User registerRequest(String username, String password, Role role, List<Tags> tags) {
        if(alreadyInDatabase(username, password))
            return null;
        if(username != null && password != null && role != null) {
            User newUser;
            /** Create new User */
            if(role.equals(Role.READER)) {
                newUser = new Reader();
            } else {
                newUser = new Journalist();
            }
            /** Set new user's fields */
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);
            newUser.setTags(tags);
            /** Add new Timeline to the Reader */
            if(role == Role.READER) {
                ((Reader) newUser).setTimeline(new Timeline());
            }
            /** Save new User */
            return userRepository.save(newUser);
        } else {
            return null;
        }
    }
    /** Method that checks the existence of a User in the repository */
    private boolean alreadyInDatabase(String username, String password) {
        for(User user : findAllElements()) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password))
                return true;
        }
        return false;
    }

    /** Login Request - returns NULL or the User corresponding to the specified dates */
    public User logInRequest(String username, String password) {
        return findByUsernameAndPassword(username, password);
    }

    /** Method that retrieves all users in the repository */
    public List<User> findAllElements(){
        return userRepository.findAll();
    }

    /** Method that finds a User having the given dates */
    public User findByUsernameAndPassword(String username, String password) {
        for(User user : findAllElements()) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    /** Method that filters all users into readers */
    public List<User> findAllReaders() {
        List<User> readers = new ArrayList<>();

        findAllElements().stream()
                .filter(user ->user.getRole() == Role.READER)
                .forEach(readers::add);
        return readers;
    }

    /** Method that filters all users having a specified tag */
    public List<User> getFilteredTag(Tags tag) {
        List<User> filtered = new ArrayList<>();

        findAllReaders().stream()
                .filter(user -> user.hasTag(tag))
                .forEach(filtered::add);
        return filtered;

    }


}
