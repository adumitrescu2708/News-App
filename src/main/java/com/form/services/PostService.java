package com.form.services;


import com.form.model.components.Post;
import com.form.model.enums.Tags;
import com.form.model.strategies.Observable;
import com.form.model.strategies.Observer;
import com.form.model.users.Reader;
import com.form.model.users.User;
import com.form.repositories.PostsRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Post Service
 * <p>
 *     MVC Service Class that handles operations on Posts repository.
 *     Using Observer pattern, the Service class extends an Observable abstract
 *     class in order to update all observers whenever new posts are available.
 *
 * </p>
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
@Component
@Service
public class PostService extends Observable {
    /** Posts Repository - Constructor Injected */
    private final PostsRepository postsRepository;
    /** Users Repository - Constructor Injected */
    private final UserService userService;

    /** Constructor */
    public PostService(PostsRepository postsRepository, UserService userService) {
        this.postsRepository = postsRepository;
        this.userService = userService;
    }

    /**
     * Post Request
     * <p>
     *     Method creates and stores a new Post object in the repository
     *     based on the data retrieved from the HTTP request
     * </p>
     *
     * @param author - Name of the Journalist
     * @param title - title of the news
     * @param tags - List of tags
     * @param text - content of the news
     * */
        public Post postRequest(String author, String title, String text, List<Tags> tags) {
            if(title != null && text != null && tags != null) {
                /** Construct new post */
                LocalDate now = LocalDate.now();
                Post newPost = new Post();
                newPost.setAuthor(author);
                newPost.setLastUpdateDate(now);
                newPost.setTags(tags);
                newPost.setTitle(title);
                newPost.setContent(text);

                /** Update observers timeline */
                notifyObservers(newPost);

                /** Store data in repository */
                return postsRepository.save(newPost);

            } else {
                return null;
            }
        }


    /**
     * Filter Posts
     * <p>
     *     Sends a list of all posts in repository having
     *     the specified tag.
     * </p>
     *
     * @param tag - tag to be filtered
     * */
    public List<Post> findAllFilteredPosts(Tags tag) {
        List<Post> filtered = new ArrayList<>();
        findAllPosts()
                .stream()
                .filter(post -> post.getTags().contains(tag))
                .forEach(filtered::add);
        return  filtered;
    }

    /**
     * Retrieves all posts from the repository
     * */
    public List<Post> findAllPosts() {
        return postsRepository.findAll();
    }

    /**
     * Update New User
     * <p>
     *     Once a new Reader is added to the repository, there are no Posts
     *     in its timeline. Therefor, at first log-in attempt this method
     *     is triggered and filters all Posts from the repository with
     *     his list of preferences and stores them chronologically
     *     in the Timeline's TreeSet
     * </p>
     * */
    public void updateNewlyAddedUser(Reader reader) {
        /** Get Instance of current user */
        User getInstance = userService.findByUsernameAndPassword(reader.getUsername(), reader.getPassword());

        /** Check if it's recently added in the repository */
        if(reader.getTimeline().getPosts().size() == 0) {
            /** Obtain a list of all Posts having the preferred tag*/
            List<Post> update = new ArrayList<>();
            for(Tags tag : getInstance.getTags()) {
                update.addAll(findAllFilteredPosts(tag));
            }
            /** Remove duplicates from the list */
            List<Post> noDuplicates = getNoDuplicates(update);

            /** Send to the Timeline */
            for(Post post : noDuplicates) {
                ((Reader)getInstance).update(post);
            }
        }

    }
    /** Method that removes duplicated from the List of posts */
    private List<Post> getNoDuplicates(List<Post> list) {
        List<Post> newList = new ArrayList<>();

        for(int i = 0; i < list.size(); i++) {
            int counter = 0;
            for(int j = i + 1; j < list.size(); j++) {
                if(list.get(i).getContent().equals(list.get(j).getContent()) &&
                        list.get(i).getTitle().equals(list.get(j).getTitle())) {
                    counter ++;
                }
            }
            if(counter == 0) {
                newList.add(list.get(i));
            }
        }
        return newList;
    }

    /** Add users to observers */
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notify
     * <p>
     *     Method uses a hashset to obtain unique references to all
     *     readers in the database that need to be updated once a new
     *     post is available, depending on its tags.
     * </p>
     * */
    @Override
    public void notifyObservers(Post post) {
        /** Obtain all readers */
        Set<User> toNotify = new HashSet<>();
        for(Tags tag : post.getTags()) {
            toNotify.addAll(userService.getFilteredTag(tag));
        }
        /** Notify Users */
        for(User user : toNotify) {
            ((Reader) user).update(post);
        }
    }

    /**
     * Update Timeline
     * <p>
     *     Once a Reader edits its preferences, the TreeSet from the
     *     Database clears and this method is triggered in order to
     *     obtain the new corresponding news
     * </p>
     * */
    public void updateTimeline(User user) {
        List<Post> update = new ArrayList<>();

        /** Obtain a list of all posts */
        for(Tags tag : user.getTags()) {
            update.addAll(findAllFilteredPosts(tag));
        }
        /** Obtain list of Posts */
        List<Post> noDuplicates = getNoDuplicates(update);

        /** Send posts to user's timeline */
        User user2 = userService.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        ((Reader) user2).getTimeline().clear();
        for(Post post : noDuplicates) {
            ((Reader)user2).update(post);
        }
    }



}
