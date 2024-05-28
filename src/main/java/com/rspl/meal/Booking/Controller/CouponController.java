package com.rspl.meal.Booking.Controller;

import com.rspl.meal.Booking.Entites.Coupon;
import com.rspl.meal.Booking.Services.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("**")
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;


    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCoupon(@RequestParam Long id) {
        try {
            Coupon coupon = couponService.generateCoupon(id);
            Map<String, String> response = new HashMap<>();
            response.put("couponId", coupon.getCouponId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate coupon. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestParam String couponId) {
        ResponseEntity<String> responseEntity = couponService.validateCoupon(couponId);
        return responseEntity;
    }
}


//package com.meal.backend.controller;
//
//import com.meal.backend.entity.Coupon;
//import com.meal.backend.service.CouponService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/coupons")
//public class CouponController {
//
//    @Autowired
//    private CouponService couponService;
//
//    @PostMapping("/generate")
//    public ResponseEntity<?> generateCoupon(@RequestParam Long employeeId) {
//        try{
//            Coupon coupon = couponService.generateCoupon(employeeId);
//            return ResponseEntity.status(HttpStatus.CREATED).body("Coupon generated successfully. Coupon id: "+ coupon.getCouponId());
//        }
//        catch(Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate coupon. Please try again later.");
//        }
//    }
//
//    @PostMapping("/validate")
//    public ResponseEntity<?> validateCoupon(@RequestBody String couponId) {
//
//        ResponseEntity<String> responseEntity = couponService.validateCoupon(couponId);
//
//
//        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
//
//        if (statusCode == HttpStatus.OK) {
//            // Coupon is valid
//            return ResponseEntity.ok("Coupon is valid!");
//        } else if (statusCode == HttpStatus.BAD_REQUEST) {
//            // Coupon is invalid
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coupon is Invalid");
//        } else if (statusCode == HttpStatus.NOT_FOUND) {
//            // Coupon not found
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found");
//        }
//
//        // If the status code is not handled above, return an internal server error
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
////        else{
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coupon is expired");
////        }
////        couponService.deleteCoupon(couponId);
////        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//}
