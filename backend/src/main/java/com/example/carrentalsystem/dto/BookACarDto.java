package com.example.carrentalsystem.dto;

import com.example.carrentalsystem.entity.BookACar;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BookACarDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date endDate;
    private Long days;
    private Long price;
    private BookCarStatus bookCarStatus;
    private Long carId;
    private Long userId;

    // for bookings
    private String username;
    private String email;

    // to show details of car booking
    private String carName;
    private String carBrand;
    private String centerName;
    private String centerCity;
    private CarResponseDto car;
    private String returnedImage;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;


}
