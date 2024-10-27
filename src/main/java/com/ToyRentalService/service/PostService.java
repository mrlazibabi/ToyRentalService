package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.PostRequest.PostBuyRequest;
import com.ToyRentalService.Dtos.Request.PostRequest.PostRentRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.CategoryRepository;
import com.ToyRentalService.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    //make a rent post
    public Post postRent(PostRentRequest postRentRequest){
        Account customer = authenticationService.getCurrentAccount();
        Post newPost = new Post();
        newPost.setToyName(postRentRequest.getToyName());
        newPost.setQuantity(postRentRequest.getQuantity());
        newPost.setImageUrl(postRentRequest.getImageUrl());
        newPost.setDescription(postRentRequest.getDescription());
        newPost.setPriceByDay(postRentRequest.getPriceByDay());
        newPost.setDepositFee(postRentRequest.getDepositFee());

        Set<Category> categories = new HashSet<>();
        for(Long idCategory: postRentRequest.getCategoryId()){
            Category category = categoryRepository.findById(idCategory)
                    .orElseThrow(() -> new NotFoundException("Category don't exist"));
            categories.add(category);
        }
        newPost.setCategories(categories);

        try{
            newPost.setStatus(Status.WAITING_FOR_APPROVAL);
            postRepository.save(newPost);
            return  newPost;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //make a rent post
    public Post postBuy(PostBuyRequest postBuyRequest){
        Account customer = authenticationService.getCurrentAccount();
        Post newPost = new Post();
        newPost.setCustomer(customer);

        newPost.setToyName(postBuyRequest.getToyName());
        newPost.setQuantity(postBuyRequest.getQuantity());
        newPost.setImageUrl(postBuyRequest.getImageUrl());
        newPost.setDescription(postBuyRequest.getDescription());
        newPost.setPrice(postBuyRequest.getPrice());

        Set<Category> categories = new HashSet<>();
        for(Long idCategory: postBuyRequest.getCategoryId()){
            Category category = categoryRepository.findById(idCategory)
                    .orElseThrow(() -> new NotFoundException("Category don't exist"));
            categories.add(category);
        }
        newPost.setCategories(categories);
        try{
            newPost.setStatus(Status.WAITING_FOR_APPROVAL);
            postRepository.save(newPost);
            return  newPost;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //approve toy post
    public Post approveToyPost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setStatus(Status.APPROVED);
            return postRepository.save(post);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    //reject toy post
    public Post rejectPost(Long id) {
        Optional<Post> toyOptional= postRepository.findById(id);
        if (toyOptional.isPresent()) {
            Post post = toyOptional.get();
            post.setStatus(Status.REJECTED);

            return postRepository.save(post);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    public Optional<Post> searchToys(String toyName, String description) {
        return postRepository.findPostByToyNameOrDescription(toyName, description);
    }

    //Read
//    public List<Post> getAllPosts(){
//        return postRepository.findAll();
//    }


//    public Page<Post> getAllPosts(String type, Status status, double minPrice, double maxPrice, Pageable pageable) {
//        Specification<Post> specification = (root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (type != null) {
//                predicates.add(criteriaBuilder.equal(root.get("type"), OrderType.valueOf(type.toUpperCase())));
//            }
//            if (status != null) {
//                predicates.add(criteriaBuilder.equal(root.get("status"), status));
//            }
//            if (minPrice >= 0) {
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//            }
//            if (maxPrice > 0) {
//                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//            }
//
//            // Chuyển đổi danh sách predicates thành mảng Predicate[] trước khi sử dụng criteriaBuilder.and()
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//
//        return postRepository.findAll(specification, pageable);
//    }
public List<Post> getAllPosts(Status status, OrderType type, Double minPrice, Double maxPrice, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return postRepository.findAll((root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        if (type != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), type));
        }
        if (minPrice != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }, pageable).getContent();
}

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id);
    }

    //Update
    public Post updatePost(long id, PostRentRequest postRentRequest){
        Post updatePost = postRepository.findPostById(id);
        if(updatePost == null){
            throw new EntityNotFoundException("Post not found!");
        }
        updatePost.setToyName(postRentRequest.getToyName());
        updatePost.setQuantity(postRentRequest.getQuantity());
        updatePost.setImageUrl(postRentRequest.getImageUrl());
        updatePost.setDescription(postRentRequest.getDescription());
        updatePost.setPriceByDay(postRentRequest.getPriceByDay());
        updatePost.setDepositFee(postRentRequest.getDepositFee());

        return postRepository.save(updatePost);
    }

    //Delete
    public void removePost(long toyId){
        Post removePost = postRepository.findPostById(toyId);
        if(removePost == null){
            throw new EntityNotFoundException("Post not found!");
        }
        removePost.setDelete(true);
        postRepository.save(removePost);
    }
}
