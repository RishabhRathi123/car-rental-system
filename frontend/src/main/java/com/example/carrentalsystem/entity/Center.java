package com.example.carrentalsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "centers")
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL)
    private List<Car> cars;
}