package com.example.carrentalsystem.services.customer;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.dto.CarDto;
import com.example.carrentalsystem.dto.CarDtoListDto;
import com.example.carrentalsystem.dto.SearchCarDto;
import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.entity.Car;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.example.carrentalsystem.exception.InvalidBookingException;
import com.example.carrentalsystem.repository.BookACarRepository;
import com.example.carrentalsystem.repository.CarRepository;
import com.example.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
        private final CarRepository carRepository;

        private final UserRepository userRepository;

        private final BookACarRepository bookACarRepository;

        @Override
        public List<CarDto> getAllCars() {
            return carRepository.findAll().stream()
                    .map(Car::getCarDto)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean bookACar(BookACarDto bookACarDto) {
            Optional<Car> optionalCar = carRepository.findById(bookACarDto.getCarId());
            Optional<User> optionalUser = userRepository.findById(bookACarDto.getUserId());
            if (optionalCar.isPresent() && optionalUser.isPresent()) {
                Car existingCar = optionalCar.get();
                BookACar bookACar = new BookACar();
                bookACar.setCar(existingCar);
                bookACar.setUser(optionalUser.get());
                bookACar.setBookCarStatus(BookCarStatus.PENDING);
                bookACar.setStartDate(bookACarDto.getStartDate());
                bookACar.setEndDate(bookACarDto.getEndDate());
                long diffInMilliSeconds = bookACarDto.getEndDate().getTime() - bookACarDto.getStartDate().getTime();
                long days = diffInMilliSeconds / (1000 * 60 * 60 * 24 );

                if (days < 1) {
                    throw new InvalidBookingException("Booking must be for at least 1 day!");
                }


                bookACar.setDays(days);
                bookACar.setPrice(existingCar.getPrice() * days);
                bookACarRepository.save(bookACar);
                return true;
            }
            return false;
        }

        @Override
        public CarDto getCarById(Long carId) {
            Optional<Car> optionalCar = carRepository.findById(carId);
            return optionalCar.map(Car::getCarDto).orElse(null);
        }

        @Override
        public List<BookACarDto> getBookingsByUserId(Long userId) {
            return bookACarRepository.findAllByUserId(userId).stream()
                    .map(BookACar::getBookACarDto)
                    .collect(Collectors.toList());
        }

    public void deleteBookingById(Long bookingId) {
        if (!bookACarRepository.existsById(bookingId)) {
            throw new RuntimeException("Booking not found");
        }
        bookACarRepository.deleteById(bookingId);
    }


    @Override
    public CarDtoListDto searchCar(SearchCarDto searchCarDto) {
        Car car= new Car();
        car.setBrand(searchCarDto.getBrand());
        car.setColor(searchCarDto.getColor());
        car.setType(searchCarDto.getType());
        car.setTransmission(searchCarDto.getTransmission());
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("brand",ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("color", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("transmission", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<Car> example = Example.of(car,matcher);
        List<Car> carList = carRepository.findAll(example);
        CarDtoListDto carDtoListDto = new CarDtoListDto();
        carDtoListDto.setCarDtoList(carList.stream().map(Car::getCarDto).collect(Collectors.toList()));
        return carDtoListDto;
    }

}
