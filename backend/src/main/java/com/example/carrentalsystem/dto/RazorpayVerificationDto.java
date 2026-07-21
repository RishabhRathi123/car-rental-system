package com.example.carrentalsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RazorpayVerificationDto {
    @JsonProperty("razorpay_order_id")
    // @JsonProperty - If you only want to ignore a property during
    // serialization (when converting an object to JSON) but not during
    // deserialization (when converting JSON to an object)
    private String razorpayOrderId;

    @JsonProperty("razorpay_payment_id")
    private String razorpayPaymentId;

    @JsonProperty("razorpay_signature")
    private String razorpaySignature;

    private Long bookingId;
}
