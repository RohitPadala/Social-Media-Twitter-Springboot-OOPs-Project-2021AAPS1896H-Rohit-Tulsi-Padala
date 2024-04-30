package com.example.SocialMedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        User user = userRepository.findById(post.getUserID()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("User does not exist"));
        }
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPost(@RequestParam("postID") int postID) {

        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("Post does not exist"), HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPostID(postID);

        Map<String, Object> postResponse = new LinkedHashMap<>();
        postResponse.put("postID", post.getPostID());
        postResponse.put("postBody", post.getPostBody());
        postResponse.put("date", post.getDate());

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

        postResponse.put("comments", commentsList);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/post")
    public ResponseEntity<?> editPost(@RequestBody Post post) {

        Post existingPost = postRepository.findById(post.getPostID()).orElse(null);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Post does not exist"));
        }

        existingPost.setPostBody(post.getPostBody());
        postRepository.save(existingPost);
        return ResponseEntity.status(HttpStatus.OK).body("Post edited successfully");
    }

    @DeleteMapping("/post")
    public ResponseEntity<?> deletePost(@RequestParam Integer postID) {

        Post existingPost = postRepository.findById(postID).orElse(null);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Post does not exist"));
        }

        postRepository.delete(existingPost);
        return ResponseEntity.status(HttpStatus.OK).body("Post deleted");
    }
}