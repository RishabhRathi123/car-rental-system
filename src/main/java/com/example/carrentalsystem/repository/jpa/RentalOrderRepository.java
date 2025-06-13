package com.example.carrentalsystem.repository.jpa;

import com.example.carrentalsystem.entity.RentalOrder;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> {

    List<RentalOrder> findByUser(User user);

    List<RentalOrder> findByVehicle(Vehicle vehicle);

    List<RentalOrder> findByStatus(RentalOrder.OrderStatus status);

    @Query("SELECT ro FROM RentalOrder ro WHERE ro.vehicle.id = :vehicleId AND " +
            "ro.status IN ('PENDING', 'CONFIRMED') AND " +
            "(ro.startDate <= :endDate AND ro.endDate >= :startDate)")
    List<RentalOrder> findConflictingOrders(@Param("vehicleId") Long vehicleId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT ro FROM RentalOrder ro WHERE ro.user.id = :userId ORDER BY ro.createdAt DESC")
    List<RentalOrder> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
