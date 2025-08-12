package com.example.carrentalsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "city")
    @JsonIgnore // for controlling how objects are converted to and from JSON (serialization and deserialization).
    // use jsonignore for - sensitive data, irrelevant data, controlling json payload size,
    // ,preventing modification - Ensuring that certain fields cannot be updated via JSON requests.
    private List<Center> centers;
}
