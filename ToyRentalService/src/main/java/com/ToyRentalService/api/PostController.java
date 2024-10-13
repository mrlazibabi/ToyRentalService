package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.PostRequest;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    PostService postService;

    //make a post
    @PostMapping()
    public ResponseEntity<Post> postToy(@Valid @RequestBody PostRequest postRequest){
        Post newPost = postService.postToy(postRequest);
        return ResponseEntity.ok(newPost);
    }

    //approve post
    @PutMapping("/{id}/approve")
    public ResponseEntity<Post> approvePost(@PathVariable long id) {
        Post approvedPost = postService.approveToyPost(id);
        return ResponseEntity.ok(approvedPost);
    }

    //reject post
    @PutMapping("/{id}/reject")
    public ResponseEntity<Post> rejectPost(@PathVariable long id) {
        Post rejectedPost = postService.rejectPost(id);
        return ResponseEntity.ok(rejectedPost);
    }

    //search post
    @GetMapping("/posts")
    public ResponseEntity<Optional  <Post>> searchPosts(
            @RequestParam(value = "toyName", required = false) String toyName,
            @RequestParam(value = "description", required = false) String description) {
        Optional<Post> toyPosts = postService.searchToys(toyName, description);
        return ResponseEntity.ok(toyPosts);
    }


    //get by id
    @GetMapping("/{id}/getPost")
    public ResponseEntity<Post> getPostById(@PathVariable long id) {
        Optional<Post> toy = postService.getPostById(id);
        return toy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //update
    @PutMapping("/{id}/update")
    public ResponseEntity<Post> updateToy(@PathVariable long id, @Valid @RequestBody PostRequest postRequest) {
        Post updatedPost = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    //detete
    @DeleteMapping("/{id}/remove")
    public ResponseEntity<Void> removeToy(@PathVariable long id) {
        postService.removePost(id);
        return ResponseEntity.noContent().build();
    }
}
