package com.form.model.components;


import com.form.model.enums.Tags;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Post
 * <p>
 *     The Post object contains the data received from the journalists.
 *     Each user's Timeline contains a set of posts.
 * </p>
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
@Entity
@Table(name = "posts_table")
public class Post {
    /** Post id */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "posts_id")
    private Long id;
    /** Post Title */
    @Column
    private String title;
    /** Post Date, for more precision could be used LocalDateTime object */
    @Column
    private LocalDate lastUpdateDate;
    /** Content of the Post */
    @Column
    private String content;
    /** List of assigned tags */
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Tags> tags = new ArrayList<>();
    /** Name of the Journalist */
    @Column
    private String author;

    /** Default Constructor */
    public Post() {

    }

    /** Copy Constructor */
    public Post(Post a) {
        this.title = a.title;
        this.lastUpdateDate = a.lastUpdateDate;
        this.content = a.content;
        this.author = a.author;
        this.tags.addAll(a.getTags());
    }

    /** Default getters and setters */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
