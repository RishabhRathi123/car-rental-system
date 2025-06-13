package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.entity.RentalOrder;
import com.example.carrentalsystem.service.RentalOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rental-orders")
@CrossOrigin(origins = "*")
public class RentalOrderController {

    private final RentalOrderService rentalOrderService;

    public RentalOrderController(RentalOrderService rentalOrderService) {
        this.rentalOrderService = rentalOrderService;
    }

    @PostMapping
    public ResponseEntity<?> createRentalOrder(@Valid @RequestBody CreateRentalOrderRequest request) {
        try {
            RentalOrder savedOrder = rentalOrderService.createRentalOrder(
                    request.getUserId(),
                    request.getVehicleId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalOrderById(@PathVariable Long id) {
        Optional<RentalOrder> order = rentalOrderService.getRentalOrderById(id);
        if (order.isPresent()) {
            return new ResponseEntity<>(order.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ErrorResponse("Rental order not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<RentalOrder>> getAllRentalOrders() {
        List<RentalOrder> orders = rentalOrderService.getAllRentalOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalOrder>> getRentalOrdersByUserId(@PathVariable Long userId) {
        List<RentalOrder> orders = rentalOrderService.getRentalOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RentalOrder>> getRentalOrdersByStatus(@PathVariable RentalOrder.OrderStatus status) {
        List<RentalOrder> orders = rentalOrderService.getRentalOrdersByStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRentalOrderStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            RentalOrder updatedOrder = rentalOrderService.updateRentalOrderStatus(id, request.getStatus());
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRentalOrder(@PathVariable Long id) {
        try {
            rentalOrderService.deleteRentalOrder(id);
            return new ResponseEntity<>(new SuccessResponse("Rental order deleted successfully"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Inner classes for request/response objects
    public static class CreateRentalOrderRequest {
        private Long userId;
        private Long vehicleId;
        private LocalDate startDate;
        private LocalDate endDate;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }

    public static class StatusUpdateRequest {
        private RentalOrder.OrderStatus status;

        public RentalOrder.OrderStatus getStatus() { return status; }
        public void setStatus(RentalOrder.OrderStatus status) { this.status = status; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
