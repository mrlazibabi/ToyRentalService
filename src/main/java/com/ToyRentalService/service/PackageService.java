package com.ToyRentalService.service;
import com.ToyRentalService.Dtos.Request.PackageRequest;
import com.ToyRentalService.Dtos.Response.ResponseObject;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Payment;
import com.ToyRentalService.entity.RentalPackage;
import com.ToyRentalService.enums.OrderType;
import com.ToyRentalService.enums.PaymentStatus;
import com.ToyRentalService.exception.exceptions.NotFoundException;
import com.ToyRentalService.repository.AccountRepository;
import com.ToyRentalService.repository.PaymentRepository;
import com.ToyRentalService.repository.RentalPackageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@Service
public class PackageService {
    @Autowired
    private RentalPackageRepository rentalPackageRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private VNPayPaymentService vnpayPaymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    public ResponseEntity<List<RentalPackage>> getAllPackages() {
        try {
            List<RentalPackage> packages = rentalPackageRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(packages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    public RentalPackage getPackageById(Long id) {
        return rentalPackageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + id));
    }

    public RentalPackage createPackage(PackageRequest request) {
        RentalPackage newPackage = new RentalPackage();
        newPackage.setPackageName(request.getPackageName());
        newPackage.setPackagePrice(request.getPackagePrice());
        newPackage.setNumberPost(request.getNumberPost());
        newPackage.setDescription(request.getDescription());
        return rentalPackageRepository.save(newPackage);
    }

    public RentalPackage updatePackage(Long id, PackageRequest request) {
        RentalPackage existingPackage = getPackageById(id);
        existingPackage.setPackageName(request.getPackageName());
        existingPackage.setPackagePrice(request.getPackagePrice());
        existingPackage.setNumberPost(request.getNumberPost());
        existingPackage.setDescription(request.getDescription());
        return rentalPackageRepository.save(existingPackage);
    }

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

//    public String initiatePackagePayment(HttpServletRequest request, Long packageId, Long accountId) {
//        RentalPackage rentalPackage = getPackageById(packageId);
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));
//
//        String returnUrl = "https://www.facebook.com/" + packageId + "/payment-return?accountId=" + accountId;
//        String orderInfo = "Payment for package: " + rentalPackage.getPackageName();
//        int amount = (int) rentalPackage.getPackagePrice();
//
//        return vnpayPaymentService.createPaymentUrl(request, amount, orderInfo, returnUrl);
//    }
public String initiatePackagePayment(HttpServletRequest request, Long packageId) {
    // Lấy accountId từ người dùng hiện tại
    Account customer = authenticationService.getCurrentAccount();
    if (customer == null) {
        throw new NotFoundException("User not logged in");
    }

    RentalPackage rentalPackage = getPackageById(packageId);

    String returnUrl = "https://www.facebook.com/" + packageId + "/payment-return";
    String orderInfo = "Payment for package: " + rentalPackage.getPackageName();
    int amount = (int) rentalPackage.getPackagePrice();

    return vnpayPaymentService.createPaymentUrl(amount, orderInfo, returnUrl);
}

//    public ResponseEntity<ResponseObject> handlePaymentReturn(HttpServletRequest request, Long accountId, Long packageId) {
//        int validationStatus = vnpayPaymentService.validatePaymentReturn(request);
//        String transactionStatus = request.getParameter("vnp_TransactionStatus");
//        String responseCode = request.getParameter("vnp_ResponseCode");
//        if (validationStatus == 1 && "00".equals(transactionStatus) && "00".equals(responseCode)) {
//            RentalPackage rentalPackage = getPackageById(packageId);
//            Account account = accountRepository.findById(accountId)
//                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));
//
//            Payment payment = new Payment();
//            payment.setPrice((float) rentalPackage.getPackagePrice());
//            payment.setPaymentStatus(PaymentStatus.COMPLETED);
//            payment.setIsDeposit(false);
//
//            paymentRepository.save(payment);
//
//            accountRepository.save(account);
//
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(new ResponseObject("Successful", "Payment successful and post count updated", null));
//        } else {
//            Payment payment = new Payment();
//            payment.setPaymentStatus(PaymentStatus.FAILED);
//            payment.setIsDeposit(false);
//            paymentRepository.save(payment);
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new ResponseObject("Failed", "Payment failed or invalid", null));
//        }
//    }
public ResponseEntity<ResponseObject> handlePaymentReturn(HttpServletRequest request, Long packageId) {
    Account account = authenticationService.getCurrentAccount();
    if (account == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseObject("Unauthorized", "User not logged in", null));
    }

    int validationStatus = vnpayPaymentService.validatePaymentReturn(request);

    if (validationStatus == 0) {
        RentalPackage rentalPackage = getPackageById(packageId);
        Payment payment = new Payment();
        payment.setPrice((float) rentalPackage.getPackagePrice());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setIsDeposit(false);
        payment.setOrderType(OrderType.BUYPOST);
        paymentRepository.save(payment);
        account.setPostCount(account.getPostCount() + rentalPackage.getNumberPost());
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("Successful", "Payment successful and post count updated", null));
    } else {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setIsDeposit(false);
        paymentRepository.save(payment);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("Failed", "Payment failed or invalid", null));
    }
}
}


