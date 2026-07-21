package com.example.carrentalsystem.repository;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.enums.BookCarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookACarRepository extends JpaRepository<BookACar, Long> {

    List<BookACar> findAllByUserId(Long userId);
    List<BookACar> findByCarIdAndBookCarStatus(Long carId, BookCarStatus status);
    Optional<BookACar> findByRazorpayOrderId(String razorpayOrderId);
}

