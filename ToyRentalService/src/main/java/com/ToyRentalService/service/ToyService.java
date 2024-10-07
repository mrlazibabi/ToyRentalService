package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.Request.ToyUpdateRequest;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.ToyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToyService {
    @Autowired
    ToyRepository toyRepository;

    //Create
    public Toy createUser(Toy toy){
        return toyRepository.save(toy);
    }

    //Read
    public List<Toy> getAllToys(){
        return toyRepository.findAll();
    }

    public Optional<Toy> getToyById(Long id) {
        return toyRepository.findById(id);
    }

    //Update
    public Toy updateToy(long toyId, ToyUpdateRequest toyUpdateRequest){
        Toy updateToy = toyRepository.findToyById(toyId);
        if(updateToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        updateToy.setToyName(toyUpdateRequest.getToyName());
        updateToy.setCategory(toyUpdateRequest.getCategory());
        updateToy.setQuantity(toyUpdateRequest.getQuantity());
        updateToy.setImage(toyUpdateRequest.getImage());
        updateToy.setPrice(toyUpdateRequest.getPrice());

        return toyRepository.save(updateToy);
    }

    //Delete
    public Toy removeToy(long toyId){
        Toy removeToy = toyRepository.findToyById(toyId);
        if(removeToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        removeToy.setDelete(true);
        return toyRepository.save(removeToy);
    }
}
