package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findPostByToyNameOrDescription(String toyName, String description);
    Post findPostById(long id);
    Page<Post> findAll(Pageable pageable);
}
