package com.example.carrentalsystem.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private int amount; // in Rs.
    private String receipt;
}

