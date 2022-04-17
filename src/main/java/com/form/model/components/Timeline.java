package com.form.model.components;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/** Timeline Class
 * <p>
 *     Each Reader has a timeline. In order to prevent the news natural order
 *     and the update version of news updating, we implemented the Observer
 *     pattern along with a TreeSet Container. The Set is used for
 *     storing the news in Chronological order, sorting the entries after
 *     their release date.
 * </p>
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
@Entity
public class Timeline {
    /** Timeline id */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /** TreeSet of posts */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Post> posts = new TreeSet<>(new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o1.getLastUpdateDate().compareTo(o2.getLastUpdateDate());
        }
    });

    /** Add a new entry in the treeSet */
    public void setNewPost(Post post) {
        Post newPost = new Post(post);
        posts.add(newPost);
    }

    /** Once a Reader updated its preferences, the TreeSet is cleared and
     * updated with the corresponding posts */
    public void clear() {
        posts.clear();
    }

    /** Basic Getters and Setters */
    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

}
