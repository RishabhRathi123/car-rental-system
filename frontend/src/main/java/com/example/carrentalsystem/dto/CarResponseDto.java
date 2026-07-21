package com.example.carrentalsystem.dto;

import lombok.Data;

@Data
public class CarResponseDto {
    private long id;
    private String brand;
    private String color;
    private String type;
    private String name;
    private String transmission;
    private String description;
    private Long price;
    private String year;

    private byte[] returnedImage;

    private Long centerId;
    private String centerName;
    private String city;
    private Long cityId;

    private Integer yearInt;     // For sorting
    private Double distance;     // From user


}
