package com.form.model.users;

import com.form.model.components.Post;
import com.form.model.components.Timeline;
import com.form.model.strategies.Observer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Reader class
 * <p>
 *      Basic Reader Class, implements Observer for Observer pattern used
 *      for updating in real time the timeline based on the posts repository
 *      status.
 * </p>
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */

@Entity
public class Reader extends User implements Observer {
    /** Timeline */
    @JoinColumn
    @OneToOne(cascade= CascadeType.ALL)
    private Timeline timeline;

    /** Update Method from Observer Pattern */
    @Override
    public void update(Post post) {
        timeline.setNewPost(post);
    }

    /** Default Getters and Setters */
    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }


}
