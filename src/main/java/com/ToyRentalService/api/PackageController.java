package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.PackageRequest;
import com.ToyRentalService.Dtos.Response.ResponseObject;
import com.ToyRentalService.entity.RentalPackage;
import com.ToyRentalService.service.PackageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/packages")
@SecurityRequirement(name = "api")
public class PackageController {
    @Autowired
    private PackageService packageService;

    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        return packageService.getAllPackages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalPackage> getPackageById(@PathVariable long id) {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }

    @PostMapping
    public ResponseEntity<RentalPackage> createPackage(@RequestBody @Valid PackageRequest packageRequest) {
        RentalPackage createdPackage = packageService.createPackage(packageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalPackage> updatePackage(@PathVariable long id, @RequestBody @Valid PackageRequest packageRequest) {
        RentalPackage updatedPackage = packageService.updatePackage(id, packageRequest);
        return ResponseEntity.ok(updatedPackage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable long id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}

