package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryById(long id);
    List<Category> findByIsDelete(boolean isDelete);
}
