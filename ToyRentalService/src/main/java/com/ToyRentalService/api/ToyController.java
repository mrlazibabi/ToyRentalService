package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.ToyUpdateRequest;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.service.ToyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/toy")
public class ToyController {
    @Autowired
    ToyService toyService;

    //make a post
    @PostMapping("/post")
    public ResponseEntity<Toy> postToy(@Valid @RequestBody Toy toy){
        Toy postToy = toyService.postToy(toy);
        return ResponseEntity.ok(postToy);
    }

    //approve post
    @PutMapping("/{id}/approve")
    public ResponseEntity<Toy> approvePost(@PathVariable long id) {
        Toy approvedToyPost = toyService.approveToyPost(id);
        return ResponseEntity.ok(approvedToyPost);
    }

    //reject post
    @PutMapping("/{id}/reject")
    public ResponseEntity<Toy> rejectPost(@PathVariable long id) {
        Toy rejectedToyPost = toyService.rejectPost(id);
        return ResponseEntity.ok(rejectedToyPost);
    }

    //search post
    @GetMapping("/posts")
    public ResponseEntity<Optional  <Toy>> searchPosts(
            @RequestParam(value = "toyName", required = false) String toyName,
            @RequestParam(value = "description", required = false) String description) {
        Optional<Toy> toyPosts = toyService.searchToys(toyName, description);
        return ResponseEntity.ok(toyPosts);
    }

    //create
//    @PostMapping("/toys")
//    public ResponseEntity<Toy> createToy(@Valid @RequestBody Toy toy){
//        Toy newToy = toyService.createToy(toy);
//        return ResponseEntity.ok(toy);
//    }

    //get all
    @GetMapping("/toys")
    public ResponseEntity<List<Toy>> getAllToys() {
        List<Toy> toys = toyService.getAllToys();
        return ResponseEntity.ok(toys);
    }

    //get by id
    @GetMapping("{id}")
    public ResponseEntity<Toy> getToyById(@PathVariable long id) {
        Optional<Toy> toy = toyService.getToyById(id);
        return toy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //update
    @PutMapping("{id}")
    public ResponseEntity<Toy> updateToy(@PathVariable long id,@Valid @RequestBody ToyUpdateRequest toyUpdateRequest) {
        Toy updatedToy = toyService.updateToy(id, toyUpdateRequest);
        return ResponseEntity.ok(updatedToy);
    }

    //detete
    @DeleteMapping("{id}")
    public ResponseEntity<Void> removeToy(@PathVariable long id) {
        toyService.removeToy(id);
        return ResponseEntity.noContent().build();
    }
}
