package com.example.SocialMedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@RestController
public class UserController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Forbidden, Account already exists"));
            } else {
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Account Creation Successful");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            if (existingUser.getPassword().equals(user.getPassword())) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Username/Password Incorrect"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User does not exist"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam("userID") Integer userID) {
        Optional<User> optionalUser = userRepository.findById(userID);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", user.getName());
            userDetails.put("userID", user.getUserID());
            userDetails.put("email", user.getEmail());
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User does not exist"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> userResponses = new ArrayList<>();
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", user.getName());
            userDetails.put("userID", user.getUserID());
            userDetails.put("email", user.getEmail());
            userResponses.add(userDetails);
        }
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/")
    public ResponseEntity<Object> getUserFeed() {
        List<Post> posts = postRepository.findAllByOrderByPostIDDesc();

        List<Object> postsResponse = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> postDetails = new LinkedHashMap<>();
            postDetails.put("postID", post.getPostID());
            postDetails.put("postBody", post.getPostBody());
            postDetails.put("date", post.getDate());

            List<Comment> comments = commentRepository.findByPostID(post.getPostID());

            List<Object> commentsList = new ArrayList<>();
            for (Comment comment : comments) {
                Map<String, Object> commentDetails = new LinkedHashMap<>();
                commentDetails.put("commentID", comment.getCommentID());
                commentDetails.put("commentBody", comment.getCommentBody());

                User user = userRepository.findById(comment.getUserID()).orElse(null);
                if (user != null) {
                    Map<String, Object> commentCreator = new LinkedHashMap<>();
                    commentCreator.put("userID", user.getUserID());
                    commentCreator.put("name", user.getName());
                    commentDetails.put("commentCreator", commentCreator);
                }
                commentsList.add(commentDetails);
            }
            postDetails.put("comments", commentsList);
            postsResponse.add(postDetails);
        }
        return ResponseEntity.ok(postsResponse);
    }

    static class ErrorResponse {
        @JsonProperty("Error")
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}