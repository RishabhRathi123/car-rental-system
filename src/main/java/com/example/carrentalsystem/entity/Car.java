package com.example.carrentalsystem.entity;

import com.example.carrentalsystem.dto.CarDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String brand;
    private String color;
    private String type;
    private String name;
    private String transmission;
    private String description;
    private Long price;
    private Date year;

    private boolean deleted = false; // for soft delete

    @Column(columnDefinition = "longblob")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;


    public CarDto getCarDto() {
        CarDto carDto = new CarDto();
        carDto.setId(this.id);
        carDto.setBrand(this.brand);
        carDto.setColor(this.color);
        carDto.setType(this.type);
        carDto.setName(this.name);
        carDto.setTransmission(this.transmission);
        carDto.setDescription(this.description);
        carDto.setPrice(this.price);
        carDto.setYear(this.year);
        carDto.setReturnedImage(this.image); // Include the image in the DTO

        return carDto;
    }
}
