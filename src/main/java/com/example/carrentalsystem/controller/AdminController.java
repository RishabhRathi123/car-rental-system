package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dto.*;
import com.example.carrentalsystem.entity.Center;
import com.example.carrentalsystem.entity.City;
import com.example.carrentalsystem.repository.CenterRepository;
import com.example.carrentalsystem.repository.CityRepository;
import com.example.carrentalsystem.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/car")
    public ResponseEntity<?> postCar(@ModelAttribute CarDto carDto) throws IOException {
        boolean success = adminService.postCar(carDto);

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/cars")
    public ResponseEntity<?> getAllCars() {
        return ResponseEntity.ok(adminService.getAllCars());
    }

    @DeleteMapping("/car/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        adminService.deleteCar(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/car/delete/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id) {
        boolean success = adminService.softDeleteCar(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Car soft-deleted succesfully"));

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found.");
        }
    }

    @GetMapping("/car/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        CarDto carDto = adminService.getCarById(id);
        return ResponseEntity.ok(carDto);
    }

    @PutMapping("/car/{carId}")
    public ResponseEntity<Void> updateCar(@PathVariable Long carId, @ModelAttribute CarDto carDto) throws IOException {
        try {
            boolean success = adminService.updateCar(carId, carDto);

            if (success) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/car/bookings")
    public ResponseEntity<List<BookACarDto>> getBookings() {
        return ResponseEntity.ok(adminService.getBookings());
    }

    @GetMapping("/car/booking/{bookingId}/{status}")
    public ResponseEntity<?> changeBookingStatus(@PathVariable Long bookingId,@PathVariable String status) {
        boolean success = adminService.changeBookingStatus(bookingId, status);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    private final CityRepository cityRepository;
    private final CenterRepository centerRepository;

    @GetMapping("/cities")
    public ResponseEntity<List<CityDto>> getCities() {
        List<City> cities = cityRepository.findAll();
        List<CityDto> cityDtos = cities.stream()
                .map(this::mapToCityDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cityDtos);
    }

    @GetMapping("/centers/by-city/{cityId}")
    public ResponseEntity<List<CenterDto>> getCentersByCity(@PathVariable Long cityId) {
        List<Center> centers = centerRepository.findByCityId(cityId);
        List<CenterDto> centerDtos = centers.stream()
                .map(this::mapToCenterDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(centerDtos);
    }

    private CityDto mapToCityDto(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        return dto;
    }

    private CenterDto mapToCenterDto(Center center) {
        CenterDto dto = new CenterDto();
        dto.setId(center.getId());
        dto.setName(center.getName());
        return dto;
    }
}
