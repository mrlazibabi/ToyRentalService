package com.ToyRentalService.service;
import com.ToyRentalService.Dtos.Request.PackageRequest;
import com.ToyRentalService.Dtos.Response.ResponseObject;
import com.ToyRentalService.entity.RentalPackage;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import com.ToyRentalService.repository.RentalPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@Service
public class PackageService {
    @Autowired
    private RentalPackageRepository rentalPackageRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Lấy tất cả các package
    public ResponseEntity<ResponseObject> getAllPackages() {
        try {
            List<RentalPackage> packages = rentalPackageRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Successful", "Found packages", packages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("Failed", "Error retrieving packages", null));
        }
    }

    // Lấy package theo ID
    public RentalPackage getPackageById(Long id) {
        return rentalPackageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + id));
    }

    // Tạo mới package
    public RentalPackage createPackage(PackageRequest request) {
        RentalPackage newPackage = new RentalPackage();
        newPackage.setPackageName(request.getPackageName());
        newPackage.setPackagePrice(request.getPackagePrice());
        newPackage.setNumberPost(request.getNumberPost());
        newPackage.setDescription(request.getDescription());
        return rentalPackageRepository.save(newPackage);
    }

    // Cập nhật package theo ID
    public RentalPackage updatePackage(Long id, PackageRequest request) {
        RentalPackage existingPackage = getPackageById(id);
        existingPackage.setPackageName(request.getPackageName());
        existingPackage.setPackagePrice(request.getPackagePrice());
        existingPackage.setNumberPost(request.getNumberPost());
        existingPackage.setDescription(request.getDescription());
        return rentalPackageRepository.save(existingPackage);
    }

    // Xóa package theo ID
    public ResponseEntity<ResponseObject> deletePackage(Long id) {
        try {
            RentalPackage existingPackage = getPackageById(id);
            rentalPackageRepository.delete(existingPackage);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("Successful", "Package deleted successfully", null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("Failed", e.getMessage(), null));
        }
    }
}


