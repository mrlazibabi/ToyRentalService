package com.ToyRentalService.service;

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
    public Toy updateToy(long id, Toy toy){
        Toy updateToy = toyRepository.findToyById(id);
        if(updateToy == null){
            throw new EntityNotFoundException("Account not found!");
        }
        updateToy.setToyName(toy.getToyName());
        updateToy.setCategory(toy.getCategory());
        updateToy.setQuantity(toy.getQuantity());
        updateToy.setImage(toy.getImage());
        updateToy.setPrice(toy.getPrice());

        return toyRepository.save(updateToy);
    }

    //Delete
    public Toy removeToy(long id){
        Toy removeToy = toyRepository.findToyById(id);
        if(removeToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        removeToy.setDelete(true);
        return toyRepository.save(removeToy);
    }
}
