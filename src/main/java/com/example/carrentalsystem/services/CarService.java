package com.example.carrentalsystem.services;

import com.example.carrentalsystem.dto.CarResponseDto;
import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.entity.Car;
import com.example.carrentalsystem.entity.Center;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.example.carrentalsystem.repository.BookACarRepository;
import com.example.carrentalsystem.repository.CarRepository;
import com.example.carrentalsystem.repository.CenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BookACarRepository bookACarRepository;

    @Autowired
    private CenterRepository centerRepository;

    public List<CarResponseDto> searchCarsNearUser(double lat, double lng, double radius,
                                                   String brand, String color, String transmission, String type,
                                                   LocalDate startDate, LocalDate endDate, String sortBy) {

        List<Center> nearbyCenters = centerRepository.findCentersNearby(lat, lng, radius);
        List<Long> centerIds = nearbyCenters.stream().map(Center::getId).toList();

        List<Car> allMatchingCars = carRepository.searchCars(centerIds, brand, color, transmission, type);

        List<CarResponseDto> availableCars = new ArrayList<>();

        for (Car car : allMatchingCars) {
            // Only include if car is available for the selected date range
            if (startDate != null && endDate != null && !isCarAvailable(car.getId(), startDate, endDate)) {
                continue; // skip this car
            }

            CarResponseDto dto = mapToResponseDto(car);
            dto.setDistance(getDistance(lat, lng, car.getCenter().getLatitude(), car.getCenter().getLongitude()));

            if (car.getYear() != null) {
                int year = car.getYear().toInstant().atZone(ZoneId.systemDefault()).getYear();
                dto.setYearInt(year);
            }
            availableCars.add(dto);
        }
        if (sortBy != null) {
            switch (sortBy) {
                case "priceLow":
                    availableCars.sort(Comparator.comparing(CarResponseDto::getPrice));
                    break;
                case "priceHigh":
                    availableCars.sort(Comparator.comparing(CarResponseDto::getPrice).reversed());
                    break;
                case "yearNew":
                    availableCars.sort(Comparator.comparing(CarResponseDto::getYearInt).reversed());
                    break;
                case "yearOld":
                    availableCars.sort(Comparator.comparing(CarResponseDto::getYearInt));
                    break;
                case "distance":
                default:
                    availableCars.sort(Comparator.comparing(CarResponseDto::getDistance));
                    break;
            }
        }


        return availableCars;
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    private boolean isCarAvailable(Long carId, LocalDate start, LocalDate end) {
        if (start == null || end == null) return true;

        List<BookACar> bookings = bookACarRepository.findByCarIdAndBookCarStatus(carId, BookCarStatus.CONFIRMED);

        for (BookACar booking : bookings) {
            LocalDate bookingStart = booking.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate bookingEnd = booking.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Check for date range overlap
            if (!bookingEnd.isBefore(start) && !bookingStart.isAfter(end)) {
                return false;
            }
        }

        return true;
    }



    public CarResponseDto mapToResponseDto(Car car) {
        CarResponseDto dto = new CarResponseDto();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setColor(car.getColor());
        dto.setType(car.getType());
        dto.setName(car.getName());
        dto.setTransmission(car.getTransmission());
        dto.setDescription(car.getDescription());
        dto.setPrice(car.getPrice());
        dto.setReturnedImage(car.getImage());
        dto.setYear(car.getYear() != null ? car.getYear().toInstant().atZone(ZoneId.systemDefault()).getYear() + "" : "N/A");

        if (car.getCenter() != null) {
            dto.setCenterId(car.getCenter().getId());
            dto.setCenterName(car.getCenter().getName());
            if (car.getCenter().getCity() != null) {
                dto.setCity(car.getCenter().getCity().getName());
            }
        }

        return dto;
    }

}
