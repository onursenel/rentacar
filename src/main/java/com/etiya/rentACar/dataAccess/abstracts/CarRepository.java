package com.etiya.rentACar.dataAccess.abstracts;

import com.etiya.rentACar.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Integer> {
    //Reflection
    //Aslında BrandImpl yok ama varmış gibi davranıyor bu noktada reflection devreye girer

    boolean existsById(int id);//true
    Optional<Car> findByPlateIgnoreCase(String plate);

    @Query(nativeQuery = true, value = "SELECT * FROM CARS CAR WHERE CAR.PLATE LIKE %:plate%")
    List<Car> searchPlate(String plate);
}