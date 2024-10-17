package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.PostRequest.PostBuyRequest;
import com.ToyRentalService.Dtos.Request.PostRequest.PostRentRequest;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/post")
@SecurityRequirement(name = "api")
public class PostController {
    @Autowired
    PostService postService;

    //make a rent post
    @PostMapping("/rent")
    public ResponseEntity<Post> postRent(@Valid @RequestBody PostRentRequest postRentRequest){
        Post newPost = postService.postRent(postRentRequest);
        return ResponseEntity.ok(newPost);
    }

    //make a buy post
    @PostMapping("buy")
    public ResponseEntity<Post> postBuy(@Valid @RequestBody PostBuyRequest postBuyRequest){
        Post newPost = postService.postBuy(postBuyRequest);
        return ResponseEntity.ok(newPost);
    }

    //approve post
    @PutMapping("/approve/{id}")
    public ResponseEntity<Post> approvePost(@PathVariable long id) {
        Post approvedPost = postService.approveToyPost(id);
        return ResponseEntity.ok(approvedPost);
    }

    //reject post
    @PutMapping("/reject/{id}")
    public ResponseEntity<Post> rejectPost(@PathVariable long id) {
        Post rejectedPost = postService.rejectPost(id);
        return ResponseEntity.ok(rejectedPost);
    }

    //search post
    @GetMapping("/search")
    public ResponseEntity<Optional  <Post>> searchPosts(
            @RequestParam(value = "toyName", required = false) String toyName,
            @RequestParam(value = "description", required = false) String description) {
        Optional<Post> toyPosts = postService.searchToys(toyName, description);
        return ResponseEntity.ok(toyPosts);
    }


    //get by id
    @GetMapping("/getPost/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable long id) {
        Optional<Post> toy = postService.getPostById(id);
        return toy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //update
//    @PutMapping("/{id}/update")
//    public ResponseEntity<Post> updateToy(@PathVariable long id, @Valid @RequestBody PostRentRequest postRequest) {
//        Post updatedPost = postService.updatePost(id, postRequest);
//        return ResponseEntity.ok(updatedPost);
//    }

    //detete
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeToy(@PathVariable long id) {
        postService.removePost(id);
        return ResponseEntity.noContent().build();
    }
}
