package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dto.CarResponseDto;
import com.example.carrentalsystem.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*") // Or your frontend domain
public class CarController {
    @Autowired
    private CarService carService;

    @GetMapping("/search-nearby")
    public List<CarResponseDto> searchNearbyCars(
            @RequestParam double lat, // @RequestParam - to bind request parameters from url's query string to method parameters
            @RequestParam double lng,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "50") double radius,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        return carService.searchCarsNearUser(
                lat, lng, radius, brand, color, transmission, type,
                startDate != null ? LocalDate.parse(startDate) : null,
                endDate != null ? LocalDate.parse(endDate) : null,
                sortBy
        );
    }


}
