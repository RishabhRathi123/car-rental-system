package com.example.carrentalsystem.services.admin;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.dto.CarDto;
import com.example.carrentalsystem.dto.CarDtoListDto;
import com.example.carrentalsystem.dto.SearchCarDto;
import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.entity.Car;
import com.example.carrentalsystem.entity.Center;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.example.carrentalsystem.repository.BookACarRepository;
import com.example.carrentalsystem.repository.CarRepository;
import com.example.carrentalsystem.repository.CenterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.jsonwebtoken.lang.Strings.clean;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final CarRepository carRepository;

    private final BookACarRepository bookACarRepository;

    private final CenterRepository centerRepository;

    @Override
    public boolean postCar(CarDto carDto) throws IOException {
        try {
            Car car = new Car();
            car.setBrand(carDto.getBrand());
            car.setColor(carDto.getColor());
            car.setType(carDto.getType());
            car.setName(carDto.getName());
            car.setTransmission(carDto.getTransmission());
            car.setDescription(carDto.getDescription());
            car.setPrice(carDto.getPrice());
            car.setYear(carDto.getYear());
            if (carDto.getImage() != null) {
                car.setImage(carDto.getImage().getBytes());
            }
            if (carDto.getCenterId() != null) {
                Center center = centerRepository.findById(carDto.getCenterId()).orElseThrow(() -> new RuntimeException("Center not found"));
                car.setCenter(center);
            }
            carRepository.save(car);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findByDeletedFalse()
                .stream()
                .map(Car::getCarDto)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteCar(Long id) { // hard delete
        carRepository.deleteById(id);
    }

    @Override
    public CarDto getCarById(Long id) {
        Optional<Car> optionalCar = carRepository.findById(id);
        return optionalCar.map(Car::getCarDto).orElse(null);
    }

    @Override
    public boolean updateCar(Long carId, CarDto carDto) throws IOException {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            car.setBrand(carDto.getBrand());
            car.setColor(carDto.getColor());
            car.setType(carDto.getType());
            car.setName(carDto.getName());
            car.setTransmission(carDto.getTransmission());
            car.setDescription(carDto.getDescription());
            car.setPrice(carDto.getPrice());
            car.setYear(carDto.getYear());
            if (carDto.getImage() != null) {
                car.setImage(carDto.getImage().getBytes());
            }
            if (carDto.getCenterId() != null) {
                Optional<Center> center = centerRepository.findById(carDto.getCenterId());
                center.ifPresent(car::setCenter);
            }
            carRepository.save(car);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public List<BookACarDto> getBookings() {
        return bookACarRepository.findAll().stream().map(BookACar::getBookACarDto).collect(Collectors.toList());
    }

    @Override
    public boolean changeBookingStatus(Long bookingId, String status) {
        Optional<BookACar> optionalBookACar = bookACarRepository.findById(bookingId);
        if (optionalBookACar.isPresent()) {
            BookACar existingBookACar = optionalBookACar.get();
            if(Objects.equals(status, "Approve")) {
                existingBookACar.setBookCarStatus(BookCarStatus.APPROVED_PENDING_PAYMENT);
            }
            else {
                existingBookACar.setBookCarStatus(BookCarStatus.REJECTED);
            }
            bookACarRepository.save(existingBookACar);
            return true;
        }
        return false;
    }



    private String clean(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }



    @Override
    public boolean softDeleteCar(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);

        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            car.setDeleted(true);
            carRepository.save(car); // Save the updated car
            return true; // Return true since deletion was applied
        }

        return false; // Only return false if car was not found
    }


}
