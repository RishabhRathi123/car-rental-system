package com.example.carrentalsystem.dto;

import lombok.Data;

@Data
public class SearchCarDto {
    private String brand;
    private String color;
    private String type;
    private String transmission;
    private String startDate;
    private String endDate;

}
