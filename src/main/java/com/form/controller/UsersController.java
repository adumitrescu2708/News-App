package com.form.controller;

import com.form.model.components.Post;

import com.form.model.users.Reader;
import com.form.model.enums.Role;
import com.form.model.enums.Tags;
import com.form.model.users.User;
import com.form.services.PostService;
import com.form.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * MVC - Controller
 *
 * <p>
 *     The main Entry in the Web Platform.
 *
 *     Uses the 2 services (wired with constructor injection) and a private User
 *     object that stores a reference to the current user in the platform. Depending
 *     on the current state in the platform, the controller triggers methods in
 *     the 2 serviced and updates repositories.
 *
 * </p>
 *
 * @author Dumitrescu Alexandra
 * @since April 2022
 * */

@Controller
public class UsersController {
    /** User service - Constructor Injected */
    private final UserService userService;
    /** Post service - Constructor Injected */
    private final PostService postService;
    /** Reference to current user in the platform */
    private User authenticate;


    /** Constructor */
    public UsersController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    /** Register Page
     *     Assigns model attributes used in <register_page.html>
     * <p>
     * @ModelAttribute("registerRequest")
     *     a default dummy User, that shall not be saved in the Users Repository
     *     and is used only for HTTP request data retrieval.
     * @ModelAttribute("roleTypes")
     *     each User has a type (e.g. Reader or Journalist), sending
     *     a list of all options for selecting the contribution to the platform
     * @ModelAttribute("roleTypes")
     *      sending a list of preferences (tag-wise) for selecting the
     *      tags
     * </p>
     * */
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerRequest", new Reader());
        model.addAttribute("roleTypes", Role.values());
        model.addAttribute("tagType", Tags.values());
        return "register_page";
    }


    /** Register
     * <p>
     *     Treated Error:
     *          Type1: The registered user is a reader and has an invalid list of
     *                 preferred tags. Therefore, the user is not passed to the
     *                 repository and the register process is reloaded.
     *          Type2: The registered user is already in the database. The new user
     *                 is not stored in the Repo and the process is reloaded.
     *      If no Error occurs, sends the user to the service
     * </p>
     * */
    @PostMapping("/register")
    public String register(@ModelAttribute User user) {

        /** Validate Err Type 1*/
        if(user.getRole() == Role.READER && user.getTags().isEmpty()) {
            return "register_error_type1";
        }

        /** Send new user to the service and therefor to the repository */
        User registeredUser = userService.registerRequest(user.getUsername(),
                                    user.getPassword(),
                                    user.getRole(),
                                    user.getTags());

        /** If the new user is a reader, attach to the observers of the Post Service */
        if(registeredUser != null) {
            if(registeredUser.getRole() == Role.READER) {
                postService.attach((Reader) registeredUser);
            }
        }

        /** Validate Err Type 2 */
        return (registeredUser == null) ? "register_error_type2" : "redirect:/login";
    }


    /** Login Page
     *      Assigns model attributes used in <login_page.html>
     * */
    @RequestMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("logInRequest", new Reader());
        return "login_page";
    }

    /** Login
     * <p>
     *     Treated Errors:
     *          Err Type1: The User is not in the Database
     *
     *     If no Error occurs, retrieves the user from the database and
     *     redirects to the homepage.
     * </p>
     * */
    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        authenticate = userService.logInRequest(user.getUsername(), user.getPassword());

        /** Validate Err 1*/
        if(authenticate == null)
            return "login_error";

        /** Redirect to corresponding homepage depending on role */
        model.addAttribute("username", authenticate.getUsername());
        if(authenticate != null) {
            switch (authenticate.getRole()) {
                case READER: return "redirect:/homepage";
                case JOUNRALIST: return "redirect:/admin/homepage";
            }
        }

        return "login_error";
    }

    /** Reader's Homepage
     *      Method sends the th objects:
     * <p>
     * @ModelAttribute("posts") - posts in the Timeline of the current logged in Reader
     * @ModelAttribute("tags")  - preferred tags of current Reader (send in case of updates)
     * </p>
     *      and treats the following Errors:
     *         Err Type1: No User is logged in
     *         Err Type2: The User is newly added in the database, therefor no news had been
     *         sent to him because he was recently added in the observers list
     *
     * */
    @GetMapping("/homepage")
    public String home(Model model) {
        /** If no user is logged in, redirecting to homepage */
        if(authenticate == null)
            return "redirect:/";

        /** Check recently added Readers */
        model.addAttribute("username", authenticate.getUsername());
        if(((Reader) authenticate).getTimeline().getPosts().isEmpty()) {
            postService.updateNewlyAddedUser((Reader) authenticate);
        }

        /** Retrieve corresponding Reader from database */
        User user = userService.findByUsernameAndPassword(authenticate.getUsername(), authenticate.getPassword());

        /** Construct Model Attributes */
        model.addAttribute("posts", ((Reader) user).getTimeline().getPosts());
        model.addAttribute("tags", user.getTags());

        return "personal_reader_page";
    }

    /** Admin (Journalist) Homepage
     * <p>
     *     Sends a new Post object, current's Journalist's username and
     *     all possible types of tags.
     * </p>
     * */
    @GetMapping("/admin/homepage")
    public String getAdminHomePage(Model model) {
        /** If no user is logged in, redirect to homepage */
        if(authenticate == null) {
            return "redirect:/";
        }

        model.addAttribute("username", authenticate.getUsername());
        model.addAttribute("post", new Post());
        model.addAttribute("tagType", Tags.values());

        return "personal_journalist_page";
    }

    @PostMapping("/admin/homepage")
    public String newPostSuccess(@ModelAttribute("post") Post post) {
        /** If no user is logged in, redirect to homepage */
        if(authenticate == null) {
            return "redirect:/";
        }

        /** Send new post to the repository */
        Post newPost = postService.postRequest(authenticate.getUsername(),
                                post.getTitle(),
                                post.getContent(),
                                post.getTags());

        return "publish_page";
    }

    @GetMapping("/homepage/edit")
    public String processNewUpdate(Model model) {
        /** If no user is logged in, redirect to homepage */
        if(authenticate == null) {
            return "redirect:/";
        }

        model.addAttribute("dummy_user", new User());
        model.addAttribute("tagType", Tags.values());

        return "edit_reader_page";
    }

    @PostMapping("/homepage/edit")
    public String saveNewUpdate(@ModelAttribute("dummy_user") User user) {
        /** If no user is logged in, redirect to homepage */
        if(authenticate == null) {
            return "redirect:/";
        }

        User authenticatedUser = userService.findByUsernameAndPassword(authenticate.getUsername(), authenticate.getPassword());
        authenticatedUser.setTags(user.getTags());
        postService.updateTimeline(authenticatedUser);
        User user2 = userService.findByUsernameAndPassword(authenticatedUser.getUsername(), authenticatedUser.getPassword());

        return "redirect:/login";
    }
}
