package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dto.PaymentRequestDto;
import com.example.carrentalsystem.dto.RazorpayVerificationDto;
import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.example.carrentalsystem.repository.BookACarRepository;
import com.example.carrentalsystem.services.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/payment")
    public class PaymentController {
        private final PaymentService paymentService;
        private final BookACarRepository bookingRepository;
        private static final Logger log = LoggerFactory.getLogger(PaymentController.class);


        @Value("${razorpay.secret}")
        private String keySecret;

        public PaymentController(PaymentService paymentService, BookACarRepository bookingRepository) {
            this.paymentService = paymentService;
            this.bookingRepository = bookingRepository;
        }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequestDto data) {
        Map<String, Object> orderData = paymentService.createOrder(data.getAmount(), data.getReceipt());
        return ResponseEntity.ok(orderData);
    }
    @PostMapping("/verify-payment")
    @Transactional
    public ResponseEntity<?> verifyPayment(@RequestBody RazorpayVerificationDto dto) {
        log.info("Verifying payment for bookingId: {}", dto.getBookingId());

        boolean isValid = paymentService.verifySignature(dto);

        if (!isValid) {
            log.error("Payment signature verification failed");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        // Fetch and confirm the booking
        Optional<BookACar> optionalBooking = bookingRepository.findById(dto.getBookingId());
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        BookACar booking = optionalBooking.get();

        // Update booking status to CONFIRMED
        booking.setBookCarStatus(BookCarStatus.CONFIRMED);
        booking.setPaymentId(dto.getRazorpayPaymentId());
        bookingRepository.save(booking);

        log.info("Booking confirmed for bookingId: {}", booking.getId());
        return ResponseEntity.ok(Map.of("message", "Payment verified and booking confirmed"));

    }


}


