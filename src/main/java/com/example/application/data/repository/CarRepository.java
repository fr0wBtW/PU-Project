package com.example.application.data.repository;

import com.example.application.data.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    @Query("select c from Car c " +
            "where lower(c.make) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.model) like lower(concat('%', :searchTerm, '%'))")
    List<Car> search(@Param("searchTerm") String searchTerm);
}
