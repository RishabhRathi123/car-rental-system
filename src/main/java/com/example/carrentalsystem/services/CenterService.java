package com.example.carrentalsystem.services;

import com.example.carrentalsystem.entity.Center;
import com.example.carrentalsystem.repository.CenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CenterService {
    @Autowired
    private CenterRepository centerRepository;

    public List<Center> getNearbyCenters(double lat, double lng, double radius) {
        return centerRepository.findCentersNearby(lat, lng, radius);
    }
}

