package com.example.carrentalsystem.services;
import org.apache.commons.codec.binary.Hex;
import com.example.carrentalsystem.dto.RazorpayVerificationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    public Map<String, Object> createOrder(int amountInRupees, String receiptId) {
        String url = "https://api.razorpay.com/v1/orders";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = razorpayKey + ":" + razorpaySecret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        headers.add("Authorization", "Basic " + new String(encodedAuth));

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amountInRupees*100); // ₹100 = 10000 paise
        body.put("currency", "INR");
        body.put("receipt", receiptId);
        body.put("payment_capture", 1);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody(); // ✅ This is now parsed as a Map

    }

    public boolean verifySignature(RazorpayVerificationDto dto) {
        try {
            String payload = dto.getRazorpayOrderId() + "|" + dto.getRazorpayPaymentId();
            String expectedSignature = hmacSHA256(payload, razorpaySecret);

            // Debug logging
            System.out.println("Payload      : " + payload);
            System.out.println("Expected Sig : " + expectedSignature);
            System.out.println("Received Sig : " + dto.getRazorpaySignature());

            return expectedSignature.equals(dto.getRazorpaySignature().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(digest); // ✅ Razorpay uses hex
    }


}
