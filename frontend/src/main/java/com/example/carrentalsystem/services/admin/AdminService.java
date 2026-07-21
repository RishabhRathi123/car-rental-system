package com.example.carrentalsystem.services.admin;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.dto.CarDto;
import com.example.carrentalsystem.dto.CarDtoListDto;
import com.example.carrentalsystem.dto.SearchCarDto;
import com.example.carrentalsystem.entity.Car;

import java.io.IOException;
import java.util.List;

public interface AdminService {
    boolean postCar(CarDto carDto) throws IOException;

    List<CarDto> getAllCars();

    void deleteCar(Long id);

    CarDto getCarById(Long id);

    boolean updateCar(Long carId, CarDto carDto) throws IOException;

    List<BookACarDto> getBookings();

    boolean changeBookingStatus(Long bookingId, String status);

    boolean softDeleteCar(Long id);
}
