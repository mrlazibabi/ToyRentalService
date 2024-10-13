package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.PostRequest;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.entity.Post;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.exception.NotFoundException;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.CategoryRepository;
import com.ToyRentalService.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    //make a post
    public Post postToy(PostRequest postRequest){
        //Post newPost = modelMapper.map(postRequest, Post.class);
        Post newPost = new Post();
        newPost.setToyName(newPost.getToyName());
        //newPost.setCategory(newPost.getCategory());
        newPost.setQuantity(newPost.getQuantity());
        newPost.setImageUrl(newPost.getImageUrl());
        newPost.setDescription(newPost.getDescription());
        newPost.setPriceByTime(newPost.getPriceByTime());
        newPost.setDepositFee(newPost.getDepositFee());

        Set<Category> categories = new HashSet<>();
        for(Long idCategory: postRequest.getCategoryId()){
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

    //search Post
//    public Page<Post> searchToys(String toyName, String description, Pageable pageable) {
//        // Sử dụng Spring Data JPA để tạo query tìm kiếm linh hoạt
//        return (Page<Post>) postRepository.findPostByNameOrDescription(toyName, description, pageable);
//    }
    public Optional<Post> searchToys(String toyName, String description) {
        return postRepository.findPostByToyNameOrDescription(toyName, description);
    }

    //Read
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id);
    }

    //Update
    public Post updatePost(long id, PostRequest postRequest){
        Post updatePost = postRepository.findPostById(id);
        if(updatePost == null){
            throw new EntityNotFoundException("Post not found!");
        }
        updatePost.setToyName(postRequest.getToyName());
        //updatePost.setCategory(postRequest.getCategory());
        updatePost.setQuantity(postRequest.getQuantity());
        updatePost.setImageUrl(Collections.singletonList(postRequest.getImageUrl()));
        updatePost.setDescription(postRequest.getDescription());
        updatePost.setPriceByTime(postRequest.getPriceByTime());
        updatePost.setDepositFee(postRequest.getDepositFee());

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
