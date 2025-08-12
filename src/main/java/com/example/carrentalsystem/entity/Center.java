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

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL) // mappedBy - relationship owned by the other entity, the "many" side.
    // cascade.CascadeType.ALL = if you save, update, or delete the parent entity, the same operations will be automatically applied to its associated child entities.
    private List<Car> cars;
}
