package com.ToyRentalService.service;

import com.ToyRentalService.Dtos.NotificationFCM;
import com.ToyRentalService.Dtos.Request.ToyRequest.ToyBuyRequest;
import com.ToyRentalService.Dtos.Request.ToyRequest.ToyRentRequest;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Category;
import com.ToyRentalService.entity.Toy;
import com.ToyRentalService.enums.ToyType;
import com.ToyRentalService.enums.Status;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.exception.exceptions.EntityNotFoundException;
import com.ToyRentalService.repository.CategoryRepository;
import com.ToyRentalService.repository.ToyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.*;

@Service
public class ToyService {
    @Autowired
    ToyRepository toyRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    NotificationService notificationService;

    //make a rent toy
    public Toy toyRent(ToyRentRequest toyRentRequest){
        Account customer = authenticationService.getCurrentAccount();
        Toy newToy = new Toy();
        newToy.setCustomer(customer);

        newToy.setToyName(toyRentRequest.getToyName());
        newToy.setFromUser(customer.getUsername());
        newToy.setQuantity(toyRentRequest.getQuantity());
        newToy.setImageUrl(toyRentRequest.getImageUrl());
        newToy.setDescription(toyRentRequest.getDescription());
        newToy.setPriceByDay(toyRentRequest.getPriceByDay());
        newToy.setDepositFee(toyRentRequest.getDepositFee());

        Set<Category> categories = new HashSet<>();
        for(Long idCategory: toyRentRequest.getCategoryId()){
            Category category = categoryRepository.findById(idCategory)
                    .orElseThrow(() -> new NotFoundException("Category don't exist"));
            categories.add(category);
        }
        newToy.setCategories(categories);

        try{
            newToy.setStatus(Status.CREATED);
            newToy.setToyType(ToyType.RENT);
            customer.decrementPostCount();
            NotificationFCM notificationFCM = new NotificationFCM();
            notificationFCM.setTitle("New Toy For Rental Created");
            notificationFCM.setMessage("Your toy rental request for " + newToy.getToyName() + " has been created.");
            notificationFCM.setFcmToken(customer.getFcmToken());

            // Send notification
            notificationService.sendNotificationToAccount(notificationFCM, customer);
            toyRepository.save(newToy);
            return newToy;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //make a buy toy
    public Toy toyBuy(ToyBuyRequest toyBuyRequest){
        Account customer = authenticationService.getCurrentAccount();
        Toy newToy = new Toy();
        newToy.setCustomer(customer);

        newToy.setToyName(toyBuyRequest.getToyName());
        newToy.setFromUser(customer.getUsername());
        newToy.setQuantity(toyBuyRequest.getQuantity());
        newToy.setImageUrl(toyBuyRequest.getImageUrl());
        newToy.setDescription(toyBuyRequest.getDescription());
        newToy.setPrice(toyBuyRequest.getPrice());

        Set<Category> categories = new HashSet<>();
        for(Long idCategory: toyBuyRequest.getCategoryId()){
            Category category = categoryRepository.findById(idCategory)
                    .orElseThrow(() -> new NotFoundException("Category don't exist"));
            categories.add(category);
        }
        newToy.setCategories(categories);
        try{
            newToy.setStatus(Status.CREATED);
            newToy.setToyType(ToyType.SELL);
            customer.decrementPostCount();

            NotificationFCM notificationFCM = new NotificationFCM();
            notificationFCM.setTitle("New Toy For Sale Created");
            notificationFCM.setMessage("Your buy request for " + newToy.getToyName() + " has been created.");
            notificationFCM.setFcmToken(customer.getFcmToken());

            // Send notification
            notificationService.sendNotificationToAccount(notificationFCM, customer);

            toyRepository.save(newToy);
            return newToy;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //approve toy post
    public Toy approveToyPost(Long id) {
        Optional<Toy> postOptional = toyRepository.findById(id);
        if (postOptional.isPresent()) {
            Toy toy = postOptional.get();
            toy.setStatus(Status.CREATED);
            return toyRepository.save(toy);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    //reject toy post
    public Toy rejectToyPost(Long id) {
        Optional<Toy> toyOptional= toyRepository.findById(id);
        if (toyOptional.isPresent()) {
            Toy toy = toyOptional.get();
            toy.setStatus(Status.REJECTED);

            return toyRepository.save(toy);
        } else {
            throw new NotFoundException("Bài đăng không tồn tại");
        }
    }

    public Optional<Toy> searchToyByName(String toyName) {
        return toyRepository.findToyByToyName(toyName);
    }

    public List<Toy> getAllToys(Status status, ToyType toyType, Double minPrice, Double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toyRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (toyType != null) {
                predicates.add(criteriaBuilder.equal(root.get("toyType"), toyType));
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


    public Optional<Toy> getToyById(long id) {
        return toyRepository.findById(id);
    }

    //Update
    public Toy updateToy(long id, ToyRentRequest toyRentRequest){
        Toy updateToy = toyRepository.findToyById(id);
        if(updateToy == null){
            throw new EntityNotFoundException("Toy not found!");
        }
        updateToy.setToyName(toyRentRequest.getToyName());
        updateToy.setQuantity(toyRentRequest.getQuantity());
        updateToy.setImageUrl(toyRentRequest.getImageUrl());
        updateToy.setDescription(toyRentRequest.getDescription());
        updateToy.setPriceByDay(toyRentRequest.getPriceByDay());
        updateToy.setDepositFee(toyRentRequest.getDepositFee());

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
