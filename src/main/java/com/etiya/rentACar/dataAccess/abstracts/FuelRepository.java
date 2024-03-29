package com.etiya.rentACar.dataAccess.abstracts;

import com.etiya.rentACar.entities.Brand;
import com.etiya.rentACar.entities.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuelRepository extends JpaRepository<Fuel,Integer> {

    boolean existsById(int id);//true olarak default gelir
    Optional<Fuel> findByNameIgnoreCase(String name);
}
