package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.entity.Vehicle;
import com.example.carrentalsystem.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.createVehicle(vehicle);
            return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isPresent()) {
            return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ErrorResponse("Vehicle not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Vehicle>> getVehiclesByBrand(@PathVariable String brand) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByBrand(brand);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Vehicle>> getVehiclesByType(@PathVariable Vehicle.VehicleType type) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Vehicle.VehicleType type) {
        List<Vehicle> vehicles = vehicleService.searchAvailableVehicles(brand, model, type);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @Valid @RequestBody Vehicle vehicle) {
        try {
            vehicle.setId(id);
            Vehicle updatedVehicle = vehicleService.updateVehicle(vehicle);
            return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateVehicleAvailability(@PathVariable Long id, @RequestBody AvailabilityRequest request) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicleAvailability(id, request.isAvailable());
            return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return new ResponseEntity<>(new SuccessResponse("Vehicle deleted successfully"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Inner classes for request/response objects
    public static class AvailabilityRequest {
        private boolean available;

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
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
