package com.example.carrentalsystem.entity;

import com.example.carrentalsystem.dto.BookACarDto;
import com.example.carrentalsystem.enums.BookCarStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;

import java.util.Base64;
import java.util.Date;

@Entity
@Data
@Table(name = "bookacar")
public class BookACar {

    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;
    private Long days;
    private Long price;

    @Enumerated(EnumType.STRING)
    private BookCarStatus bookCarStatus= BookCarStatus.PENDING;

    private String paymentId;

    @Column(name = "razorpay_order_id", unique = true)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;


    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @JsonIgnore
    private Car car;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    public BookACarDto getBookACarDto() {
        BookACarDto bookACarDto = new BookACarDto();
        bookACarDto.setId(this.id);
        bookACarDto.setStartDate(this.startDate);
        bookACarDto.setEndDate(this.endDate);
        bookACarDto.setDays(this.days);
        bookACarDto.setPrice(this.price);
        bookACarDto.setBookCarStatus(this.bookCarStatus);
        bookACarDto.setCarId(this.car.getId());
        bookACarDto.setUserId(this.user.getId());
        bookACarDto.setUsername(this.user.getUsername());
        bookACarDto.setEmail(this.user.getUserEmail());

        bookACarDto.setCarName(this.car.getName());
        bookACarDto.setCarBrand(this.car.getBrand());

        bookACarDto.setCenterName(this.car.getCenter().getName());
        bookACarDto.setCenterCity(this.car.getCenter().getCity().getName());


        if (this.car.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(this.car.getImage());
            bookACarDto.setReturnedImage(base64Image);
        }

        return bookACarDto;
    }

}
