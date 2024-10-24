package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.PostRequest.PostBuyRequest;
import com.ToyRentalService.Dtos.Request.PostRequest.PostRentRequest;
import com.ToyRentalService.entity.Account;
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
    public List<Post> getAllPosts(){
        return postRepository.findAll();
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
