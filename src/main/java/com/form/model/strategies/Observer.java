package com.form.model.strategies;

import com.form.model.components.Post;
/**
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */
public interface Observer {
    void update(Post post);
}
