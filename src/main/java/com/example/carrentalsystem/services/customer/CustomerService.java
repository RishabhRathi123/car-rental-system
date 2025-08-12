package com.example.carrentalsystem.services.customer;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.dto.CarDto;
import com.example.carrentalsystem.dto.CarDtoListDto;
import com.example.carrentalsystem.dto.SearchCarDto;

import java.util.List;

public interface CustomerService {
    List<CarDto> getAllCars();

    boolean bookACar(BookACarDto bookACarDto);

    CarDto getCarById(Long carId);

    List<BookACarDto> getBookingsByUserId(Long userId);

    CarDtoListDto searchCar(SearchCarDto searchCarDto);

    void deleteBookingById(Long bookingId);
}
