package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.ToyUpdateRequest;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.exception.NotFoundException;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.ToyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ToyService {
    @Autowired
    ToyRepository toyRepository;

    //make a post
    public Toy postToy(Toy toy){
        toy.setStatus(Status.WAITING_FOR_APPROVAL);
        return toyRepository.save(toy);
    }

    //approve toy post
    public Toy approveToyPost(Long id) {
        Optional<Toy> toyOptional = toyRepository.findById(id);
        if (toyOptional.isPresent()) {
            Toy toy = toyOptional.get();
            toy.setStatus(Status.APPROVED);

            return toyRepository.save(toy);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    //reject toy post
    public Toy rejectPost(Long id) {
        Optional<Toy> toyOptional= toyRepository.findById(id);
        if (toyOptional.isPresent()) {
            Toy toy = toyOptional.get();
            toy.setStatus(Status.REJECTED);

            return toyRepository.save(toy);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    //search Post
//    public Page<Toy> searchToys(String toyName, String description, Pageable pageable) {
//        // Sử dụng Spring Data JPA để tạo query tìm kiếm linh hoạt
//        return (Page<Toy>) toyRepository.findByToyNameOrDescription(toyName, description, pageable);
//    }
    public Optional<Toy> searchToys(String toyName, String description) {
        return toyRepository.findByToyNameOrDescription(toyName, description);
    }


    //Create
    public Toy createToy(Toy toy){
        return toyRepository.save(toy);
    }

    //Read
    public List<Toy> getAllToys(){
        return toyRepository.findAll();
    }

    public Optional<Toy> getToyById(long id) {
        return toyRepository.findById(id);
    }

    //Update
    public Toy updateToy(long id, ToyUpdateRequest toyUpdateRequest){
        Toy updateToy = toyRepository.findToyById(id);
        if(updateToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        updateToy.setToyName(toyUpdateRequest.getToyName());
        updateToy.setCategory(toyUpdateRequest.getCategory());
        updateToy.setQuantity(toyUpdateRequest.getQuantity());
        updateToy.setImageUrl(Collections.singletonList(toyUpdateRequest.getImageUrl()));
        updateToy.setDescription(toyUpdateRequest.getDescription());
        updateToy.setPriceByTime(toyUpdateRequest.getPriceByTime());
        updateToy.setDepositFee(toyUpdateRequest.getDepositFee());

        return toyRepository.save(updateToy);
    }

    //Delete
    public void removeToy(long toyId){
        Toy removeToy = toyRepository.findToyById(toyId);
        if(removeToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        removeToy.setDelete(true);
        toyRepository.save(removeToy);
    }
}
