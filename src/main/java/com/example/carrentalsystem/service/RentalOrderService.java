package com.example.carrentalsystem.service;

import com.example.carrentalsystem.entity.RentalOrder;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.entity.Vehicle;
import com.example.carrentalsystem.repository.jpa.RentalOrderRepository;
import com.example.carrentalsystem.repository.jdbc.RentalOrderJdbcRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalOrderService {

    private final RentalOrderRepository rentalOrderRepository;
    private final RentalOrderJdbcRepository rentalOrderJdbcRepository;
    private final UserService userService;
    private final VehicleService vehicleService;

    @Value("${spring.profiles.active:jpa}")
    private String activeProfile;

    public RentalOrderService(RentalOrderRepository rentalOrderRepository,
                              RentalOrderJdbcRepository rentalOrderJdbcRepository,
                              UserService userService,
                              VehicleService vehicleService) {
        this.rentalOrderRepository = rentalOrderRepository;
        this.rentalOrderJdbcRepository = rentalOrderJdbcRepository;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    public RentalOrder createRentalOrder(Long userId, Long vehicleId, LocalDate startDate, LocalDate endDate) {
        // Validate user
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Validate vehicle
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            throw new RuntimeException("Vehicle not found with id: " + vehicleId);
        }

        Vehicle vehicle = vehicleOpt.get();
        if (!vehicle.getAvailable()) {
            throw new RuntimeException("Vehicle is not available for rental");
        }

        // Validate dates
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }

        // Check for conflicting reservations
        if (hasConflictingReservation(vehicleId, startDate, endDate)) {
            throw new RuntimeException("Vehicle is already booked for the selected dates");
        }

        // Calculate rental details
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end date
        BigDecimal totalAmount = vehicle.getDailyRate().multiply(BigDecimal.valueOf(totalDays));

        // Create rental order
        RentalOrder rentalOrder = new RentalOrder();
        rentalOrder.setUser(userOpt.get());
        rentalOrder.setVehicle(vehicle);
        rentalOrder.setStartDate(startDate);
        rentalOrder.setEndDate(endDate);
        rentalOrder.setTotalDays((int) totalDays);
        rentalOrder.setDailyRate(vehicle.getDailyRate());
        rentalOrder.setTotalAmount(totalAmount);
        rentalOrder.setStatus(RentalOrder.OrderStatus.PENDING);

        // Save rental order
        RentalOrder savedOrder;
        if ("jdbc".equals(activeProfile)) {
            savedOrder = rentalOrderJdbcRepository.save(rentalOrder);
        } else {
            savedOrder = rentalOrderRepository.save(rentalOrder);
        }

        // Update vehicle availability
        vehicleService.updateVehicleAvailability(vehicleId, false);

        return savedOrder;
    }

    public Optional<RentalOrder> getRentalOrderById(Long id) {
        if ("jdbc".equals(activeProfile)) {
            return rentalOrderJdbcRepository.findById(id);
        } else {
            return rentalOrderRepository.findById(id);
        }
    }

    public List<RentalOrder> getAllRentalOrders() {
        if ("jdbc".equals(activeProfile)) {
            return rentalOrderJdbcRepository.findAll();
        } else {
            return rentalOrderRepository.findAll();
        }
    }

    public List<RentalOrder> getRentalOrdersByUserId(Long userId) {
        if ("jdbc".equals(activeProfile)) {
            return rentalOrderJdbcRepository.findByUserId(userId);
        } else {
            return rentalOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
    }

    public List<RentalOrder> getRentalOrdersByStatus(RentalOrder.OrderStatus status) {
        if ("jdbc".equals(activeProfile)) {
            return rentalOrderJdbcRepository.findByStatus(status);
        } else {
            return rentalOrderRepository.findByStatus(status);
        }
    }

    public RentalOrder updateRentalOrderStatus(Long orderId, RentalOrder.OrderStatus status) {
        Optional<RentalOrder> orderOpt = getRentalOrderById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Rental order not found with id: " + orderId);
        }

        RentalOrder order = orderOpt.get();
        RentalOrder.OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);

        // Update rental order
        RentalOrder updatedOrder;
        if ("jdbc".equals(activeProfile)) {
            updatedOrder = rentalOrderJdbcRepository.save(order);
        } else {
            updatedOrder = rentalOrderRepository.save(order);
        }

        // Update vehicle availability based on status change
        if (status == RentalOrder.OrderStatus.CANCELLED || status == RentalOrder.OrderStatus.COMPLETED) {
            vehicleService.updateVehicleAvailability(order.getVehicle().getId(), true);
        } else if (previousStatus == RentalOrder.OrderStatus.CANCELLED &&
                (status == RentalOrder.OrderStatus.PENDING || status == RentalOrder.OrderStatus.CONFIRMED)) {
            vehicleService.updateVehicleAvailability(order.getVehicle().getId(), false);
        }

        return updatedOrder;
    }

    public void deleteRentalOrder(Long id) {
        Optional<RentalOrder> orderOpt = getRentalOrderById(id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Rental order not found with id: " + id);
        }

        RentalOrder order = orderOpt.get();

        // Make vehicle available again
        vehicleService.updateVehicleAvailability(order.getVehicle().getId(), true);

        if ("jdbc".equals(activeProfile)) {
            rentalOrderJdbcRepository.deleteById(id);
        } else {
            rentalOrderRepository.deleteById(id);
        }
    }

    private boolean hasConflictingReservation(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        if ("jdbc".equals(activeProfile)) {
            // For JDBC, implement custom logic to check conflicts
            List<RentalOrder> orders = rentalOrderJdbcRepository.findAll();
            return orders.stream()
                    .filter(order -> order.getVehicle().getId().equals(vehicleId))
                    .filter(order -> order.getStatus() == RentalOrder.OrderStatus.PENDING ||
                            order.getStatus() == RentalOrder.OrderStatus.CONFIRMED)
                    .anyMatch(order -> !(endDate.isBefore(order.getStartDate()) || startDate.isAfter(order.getEndDate())));
        } else {
            List<RentalOrder> conflictingOrders = rentalOrderRepository.findConflictingOrders(vehicleId, startDate, endDate);
            return !conflictingOrders.isEmpty();
        }
    }
}
