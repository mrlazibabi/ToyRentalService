package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.AccountUpdateRequest;
import com.ToyRentalService.Dtos.Request.ToyUpdateRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.service.ToyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/toy")
public class ToyController {
    @Autowired
    ToyService toyService;

    //get all
    @GetMapping
    public ResponseEntity<List<Toy>> getAllToys() {
        List<Toy> toys = toyService.getAllToys();
        return ResponseEntity.ok(toys);
    }

    //get by id
    @GetMapping("{toyId}")
    public ResponseEntity<Toy> getToyById(@PathVariable Long id) {
        Optional<Toy> toy = toyService.getToyById(id);
        return toy.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //update
    @PutMapping("{toyId}")
    public ResponseEntity<?> updateToy(@PathVariable Long id, @RequestBody ToyUpdateRequest toyUpdateRequest) {
        Toy updatedToy = toyService.updateToy(id, toyUpdateRequest);
        return ResponseEntity.ok("Toy updated successfully");
    }

    //detete
    @DeleteMapping("{toyId}")
    public ResponseEntity<Void> removeToy(@PathVariable Long id) {
        toyService.removeToy(id);
        return ResponseEntity.noContent().build();
    }
}
