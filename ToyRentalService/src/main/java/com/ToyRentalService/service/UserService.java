package com.ToyRentalService.service;

import com.ToyRentalService.entity.User;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    //Create
    public User createUser(User user){
        User newUser = userRepository.save(user);
        return newUser;
    }

    //Read
    public List<User> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }

    //Update
    public User updateUser(long id, User user){
        User updateUser = userRepository.findUserById(id);
        if(updateUser == null){
            throw new EntityNotFoundException("User not found!");
        }
        updateUser.setName(user.getName());
        return userRepository.save(updateUser);
    }

    //Delete
    public User removeUser(long id){
        User removeUser = userRepository.findUserById(id);
        if(removeUser == null){
            throw new EntityNotFoundException("User not found!");
        }
        removeUser.setDelete(true);
        return userRepository.save(removeUser);
    }
}
