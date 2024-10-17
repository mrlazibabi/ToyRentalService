package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Request.CategoryRequest;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    //create
    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        Category newCategory = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(newCategory);
    }

    //get all
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    //update
    @PutMapping("/{id}/update")
    public ResponseEntity<Category> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    //detete
    @DeleteMapping("{id}")
    public ResponseEntity<Void> removeCategory(@PathVariable long id) {
        categoryService.removeCategory(id);
        return ResponseEntity.noContent().build();
    }
}
