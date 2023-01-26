package com.example.application.data.repository;

import com.example.application.data.entity.CarService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarServiceRepository extends JpaRepository<CarService, Integer> {

}