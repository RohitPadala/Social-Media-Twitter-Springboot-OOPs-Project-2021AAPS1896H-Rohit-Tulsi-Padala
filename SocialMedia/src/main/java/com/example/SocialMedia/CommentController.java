package com.example.SocialMedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestBody Comment commentRequest) {

        User user = userRepository.findById(commentRequest.getUserID()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("User does not exist"), HttpStatus.NOT_FOUND);
        }

        Post post = postRepository.findById(commentRequest.getPostID()).orElse(null);
        if (post == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("Post does not exist"), HttpStatus.NOT_FOUND);
        }

        Comment comment = new Comment();
        comment.setCommentBody(commentRequest.getCommentBody());
        comment.setPostID(commentRequest.getPostID());
        comment.setUserID(commentRequest.getUserID());
        commentRepository.save(comment);

        return new ResponseEntity<>("Comment created successfully", HttpStatus.CREATED);
    }
    @PatchMapping("/comment")
    public ResponseEntity<?> editComment(@RequestBody Comment commentEditRequest) {
        Comment comment = commentRepository.findById(commentEditRequest.getCommentID()).orElse(null);

        if (comment == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("Comment does not exist"), HttpStatus.NOT_FOUND);
        }

        comment.setCommentBody(commentEditRequest.getCommentBody());
        commentRepository.save(comment);

        return new ResponseEntity<>("Comment edited successfully", HttpStatus.OK);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(@RequestParam("commentID") int commentID) {
        Comment comment = commentRepository.findById(commentID).orElse(null);

        if (comment == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("Comment does not exist"), HttpStatus.NOT_FOUND);
        }

        commentRepository.delete(comment);
        return new ResponseEntity<>("Comment deleted", HttpStatus.OK);
    }

    @GetMapping("/comment")
    public ResponseEntity<?> getComment(@RequestParam("commentID") int commentID) {

        Comment comment = commentRepository.findById(commentID).orElse(null);
        if (comment == null) {
            return new ResponseEntity<>(new UserController.ErrorResponse("Comment does not exist"), HttpStatus.NOT_FOUND);
        }

        User user = userRepository.findById(comment.getUserID()).orElse(null);

        Map<String, Object> commentResponse = new LinkedHashMap<>();
        commentResponse.put("commentID", comment.getCommentID());
        commentResponse.put("commentBody", comment.getCommentBody());

        Map<String, Object> commentCreator = new LinkedHashMap<>();
        commentCreator.put("userID", user.getUserID());
        commentCreator.put("name", user.getName());
        commentResponse.put("commentCreator", commentCreator);

        return ResponseEntity.ok(commentResponse);
    }
}