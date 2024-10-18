package com.ToyRentalService.repository;

import com.ToyRentalService.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findPostByToyNameOrDescription(String toyName, String description);
    Post findPostById(long id);
}
