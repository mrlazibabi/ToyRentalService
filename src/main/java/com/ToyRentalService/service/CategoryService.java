package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.CategoryRequest;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    //Create
    public Category createCategory(CategoryRequest categoryRequest){
        Category newCategory = modelMapper.map(categoryRequest, Category.class);
        try{
            categoryRepository.save(newCategory);
            return newCategory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Read
//    public List<Category> getAllCategories(){
//        return categoryRepository.findAll();
//    }
    public List<Category> getAllCategories() {
        return categoryRepository.findByIsDelete(false);
    }

    //Read id
    public Category getCategoryById(long id) {
        return categoryRepository.findCategoryById(id);
    }

    //Update
    public Category updateCategory(long id, CategoryRequest categoryRequest){
        Category updateCategory = categoryRepository.findCategoryById(id);
        if(updateCategory == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        updateCategory.setCategoryName(categoryRequest.getCategoryName());
        updateCategory.setDescription(categoryRequest.getDescription());

        return categoryRepository.save(updateCategory);
    }

    //Delete
    public void removeCategory(long id){
        Category removeCategory = categoryRepository.findCategoryById(id);
        if(removeCategory == null){
            throw new EntityNotFoundException("Category not found!");
        }
        removeCategory.setDelete(true);
        categoryRepository.save(removeCategory);
    }
}
