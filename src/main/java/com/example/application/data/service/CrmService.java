package com.example.application.data.service;

import com.example.application.data.entity.Car;
import com.example.application.data.entity.CarService;
import com.example.application.data.entity.Person;
import com.example.application.data.repository.CarServiceRepository;
import com.example.application.data.repository.PersonRepository;
import com.example.application.data.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrmService {

    private final PersonRepository personRepository;
    private final CarServiceRepository carServiceRepository;
    private final CarRepository carRepository;

    public CrmService(PersonRepository personRepository,
                      CarServiceRepository carServiceRepository,
                      CarRepository carRepository) {
        this.personRepository = personRepository;
        this.carServiceRepository = carServiceRepository;
        this.carRepository = carRepository;
    }

    public List<Person> findAllPersons(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personRepository.findAll();
        } else {
            return personRepository.search(stringFilter);
        }
    }

    public List<Person> findAllPersonsByPhoneNumber(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personRepository.findAll();
        } else {
            return personRepository.searchByPhoneNumber(stringFilter);
        }
    }

    public List<Person> findAllPersonsByCar(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personRepository.findAll();
        } else {
            return personRepository.searchByCar(stringFilter);
        }
    }

    public long countPersons() {
        return personRepository.count();
    }

    public void deletePerson(Person person) {
        personRepository.delete(person);
    }

    public void savePerson(Person person) {
        if (person == null) {
            System.err.println("Моля въведи собственик...");
            return;
        }
        personRepository.save(person);
    }

    public void deleteCar(Car car) {
        carRepository.delete(car);
    }

    public void saveCar(Car car) {
        if (car == null) {
            System.err.println("Моля въведи автомобил...");
            return;
        }
        carRepository.save(car);
    }

    public void deleteCarService(CarService carService) {
        carServiceRepository.delete(carService);
    }

    public void saveCarService(CarService carService) {
        if (carService == null) {
            System.err.println("Моля въведи сервиз...");
            return;
        }
        carServiceRepository.save(carService);
    }

    public List<CarService> findAllCarServices() {
        return carServiceRepository.findAll();
    }

    public List<Car> findAllCars(){
        return carRepository.findAll();
    }

    public List<Car> findAllCars(String stringFilter){
        if (stringFilter == null || stringFilter.isEmpty()) {
            return carRepository.findAll();
        } else {
            return carRepository.search(stringFilter);
        }
    }
}
