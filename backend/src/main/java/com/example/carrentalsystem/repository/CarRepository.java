package com.example.carrentalsystem.repository;

import com.example.carrentalsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // For search logic with soft delete
    @Query("SELECT c FROM Car c WHERE " +
            "c.center.id IN :centerIds AND " +
            "(:brand IS NULL OR LOWER(c.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:color IS NULL OR LOWER(c.color) LIKE LOWER(CONCAT('%', :color, '%'))) AND " +
            "(:transmission IS NULL OR LOWER(c.transmission) LIKE LOWER(CONCAT('%', :transmission, '%'))) AND " +
            "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
            "c.deleted = false")
    List<Car> searchCars(@Param("centerIds") List<Long> centerIds,
                         @Param("brand") String brand,
                         @Param("color") String color,
                         @Param("transmission") String transmission,
                         @Param("type") String type);


    List<Car> findByDeletedFalse();
}
