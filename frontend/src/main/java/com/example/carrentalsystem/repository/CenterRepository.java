package com.example.carrentalsystem.repository;

import com.example.carrentalsystem.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

    @Query("SELECT c FROM Center c WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(c.latitude)) * " +
            "cos(radians(c.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.latitude)))) < :radius")
    List<Center> findCentersNearby(@Param("lat") double lat,
                                   @Param("lng") double lng,
                                   @Param("radius") double radius);
    List<Center> findByCityId(Long cityId);
}
