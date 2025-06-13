package com.example.carrentalsystem.service;

import com.example.carrentalsystem.entity.Vehicle;
import com.example.carrentalsystem.repository.jpa.VehicleRepository;
import com.example.carrentalsystem.repository.jdbc.VehicleJdbcRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleJdbcRepository vehicleJdbcRepository;

    @Value("${spring.profiles.active:jpa}")
    private String activeProfile;

    public VehicleService(VehicleRepository vehicleRepository, VehicleJdbcRepository vehicleJdbcRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleJdbcRepository = vehicleJdbcRepository;
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        // Check for duplicate license plate
        if (getVehicleByLicensePlate(vehicle.getLicensePlate()).isPresent()) {
            throw new RuntimeException("License plate already exists: " + vehicle.getLicensePlate());
        }

        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.save(vehicle);
        } else {
            return vehicleRepository.save(vehicle);
        }
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findById(id);
        } else {
            return vehicleRepository.findById(id);
        }
    }

    public Optional<Vehicle> getVehicleByLicensePlate(String licensePlate) {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findByLicensePlate(licensePlate);
        } else {
            return vehicleRepository.findByLicensePlate(licensePlate);
        }
    }

    public List<Vehicle> getAllVehicles() {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findAll();
        } else {
            return vehicleRepository.findAll();
        }
    }

    public List<Vehicle> getAvailableVehicles() {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findAvailable();
        } else {
            return vehicleRepository.findByAvailableTrue();
        }
    }

    public List<Vehicle> getVehiclesByBrand(String brand) {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findByBrand(brand);
        } else {
            return vehicleRepository.findByBrandIgnoreCase(brand);
        }
    }

    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType vehicleType) {
        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.findByVehicleType(vehicleType);
        } else {
            return vehicleRepository.findByVehicleType(vehicleType);
        }
    }

    public List<Vehicle> searchAvailableVehicles(String brand, String model, Vehicle.VehicleType vehicleType) {
        if ("jdbc".equals(activeProfile)) {
            // For JDBC, implement custom search logic
            List<Vehicle> vehicles = getAvailableVehicles();
            return vehicles.stream()
                    .filter(vehicle -> brand == null || vehicle.getBrand().toLowerCase().contains(brand.toLowerCase()))
                    .filter(vehicle -> model == null || vehicle.getModel().toLowerCase().contains(model.toLowerCase()))
                    .filter(vehicle -> vehicleType == null || vehicle.getVehicleType().equals(vehicleType))
                    .toList();
        } else {
            return vehicleRepository.findAvailableVehicles(brand, model, vehicleType);
        }
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        // Check if vehicle exists
        Optional<Vehicle> existingVehicle = getVehicleById(vehicle.getId());
        if (existingVehicle.isEmpty()) {
            throw new RuntimeException("Vehicle not found with id: " + vehicle.getId());
        }

        // Check for duplicate license plate (excluding current vehicle)
        Optional<Vehicle> vehicleWithSamePlate = getVehicleByLicensePlate(vehicle.getLicensePlate());
        if (vehicleWithSamePlate.isPresent() && !vehicleWithSamePlate.get().getId().equals(vehicle.getId())) {
            throw new RuntimeException("License plate already exists: " + vehicle.getLicensePlate());
        }

        if ("jdbc".equals(activeProfile)) {
            return vehicleJdbcRepository.save(vehicle);
        } else {
            return vehicleRepository.save(vehicle);
        }
    }

    public void deleteVehicle(Long id) {
        if (!getVehicleById(id).isPresent()) {
            throw new RuntimeException("Vehicle not found with id: " + id);
        }

        if ("jdbc".equals(activeProfile)) {
            vehicleJdbcRepository.deleteById(id);
        } else {
            vehicleRepository.deleteById(id);
        }
    }

    public Vehicle updateVehicleAvailability(Long vehicleId, boolean available) {
        Optional<Vehicle> vehicleOpt = getVehicleById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle not found with id: " + vehicleId);
        }

        Vehicle vehicle = vehicleOpt.get();
        vehicle.setAvailable(available);
        return updateVehicle(vehicle);
    }
}

