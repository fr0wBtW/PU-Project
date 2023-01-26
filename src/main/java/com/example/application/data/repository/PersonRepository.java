package com.example.application.data.repository;

import com.example.application.data.entity.Person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    @Query("select c from Person c " +
        "where lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))")
    List<Person> search(@Param("searchTerm") String searchTerm);

    @Query("select c from Person c " +
            "where lower(c.phoneNumber) like lower(concat('%', :searchTerm, '%'))")
    List<Person> searchByPhoneNumber(@Param("searchTerm") String searchTerm);

    @Query(value = "select p from Person p left join Car c ON p.car_id = c.id where lower(c.make) like lower(concat('%', :searchTerm, '%'))",
    nativeQuery = true)
    List<Person> searchByCar(@Param("searchTerm") String searchTerm);
}
