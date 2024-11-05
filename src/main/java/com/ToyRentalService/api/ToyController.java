package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.ToyRequest.ToyBuyRequest;
import com.ToyRentalService.Dtos.Request.ToyRequest.ToyRentRequest;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.enums.ToyType;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.service.ToyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/toy")
@SecurityRequirement(name = "api")
public class ToyController {
    @Autowired
    ToyService toyService;


    //make a rent toy
    @PostMapping("/rent")
    public ResponseEntity<Toy> toyRent(@Valid @RequestBody ToyRentRequest toyRentRequest){
        Toy newToy = toyService.toyRent(toyRentRequest);
        return ResponseEntity.ok(newToy);
    }

    //make a buy toy
    @PostMapping("/buy")
    public ResponseEntity<Toy> toyBuy(@Valid @RequestBody ToyBuyRequest toyBuyRequest){
        Toy newToy = toyService.toyBuy(toyBuyRequest);
        return ResponseEntity.ok(newToy);
    }

    @GetMapping
    public ResponseEntity<List<Toy>> getAllToys(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) ToyType type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Toy> toys = toyService.getAllToys(status, type, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(toys);
    }

    //approve toy
    @PutMapping("/approve/{id}")
    public ResponseEntity<Toy> approveToy(@PathVariable long id) {
        Toy approvedToy = toyService.approveToyPost(id);
        return ResponseEntity.ok(approvedToy);
    }

    //reject toy
    @PutMapping("/reject/{id}")
    public ResponseEntity<Toy> rejectToy(@PathVariable long id) {
        Toy rejectedToy = toyService.rejectToyPost(id);
        return ResponseEntity.ok(rejectedToy);
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<Toy> getToyById(@PathVariable long id) {
        Optional<Toy> toy = toyService.getToyById(id);
        return toy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    //detete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeToy(@PathVariable long id) {
        toyService.removeToy(id);
        return ResponseEntity.noContent().build();
    }
}
