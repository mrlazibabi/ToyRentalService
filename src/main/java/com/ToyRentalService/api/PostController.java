package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.PostRequest.PostBuyRequest;
import com.ToyRentalService.Dtos.Request.PostRequest.PostRentRequest;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PostMapping("/buy")
    public ResponseEntity<Post> postBuy(@Valid @RequestBody PostBuyRequest postBuyRequest){
        Post newPost = postService.postBuy(postBuyRequest);
        return ResponseEntity.ok(newPost);
    }

//    @GetMapping
//    public ResponseEntity<Page<Post>> getAllPosts(
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) Status status,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Post> posts = postService.getAllPosts(type, status, minPrice, maxPrice, pageable);
//        return ResponseEntity.ok(posts);
//    }
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Post> posts = postService.getAllPosts(status, type, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(posts);
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
//    @GetMapping("/search")
//    public ResponseEntity<Optional  <Post>> searchPosts(
//            @RequestParam(value = "toyName", required = false) String toyName,
//            @RequestParam(value = "description", required = false) String description) {
//        Optional<Post> toyPosts = postService.searchToys(toyName, description);
//        return ResponseEntity.ok(toyPosts);
//    }

    //get all
//    @GetMapping()
//    public ResponseEntity<List<Post>> getAllPosts() {
//        List<Post> posts = postService.getAllPosts();
//        return ResponseEntity.ok(posts);
//    }


    //get by id
    @GetMapping("/{id}")
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeToy(@PathVariable long id) {
        postService.removePost(id);
        return ResponseEntity.noContent().build();
    }
}
