package com.example.carrentalsystem.repository.jpa;

import com.example.carrentalsystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByAvailableTrue();

    List<Vehicle> findByBrandIgnoreCase(String brand);

    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    @Query("SELECT v FROM Vehicle v WHERE v.available = true AND " +
            "(:brand IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
            "(:vehicleType IS NULL OR v.vehicleType = :vehicleType)")
    List<Vehicle> findAvailableVehicles(@Param("brand") String brand,
                                        @Param("model") String model,
                                        @Param("vehicleType") Vehicle.VehicleType vehicleType);
}
