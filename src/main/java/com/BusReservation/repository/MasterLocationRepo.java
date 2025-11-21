package com.BusReservation.repository;

import com.BusReservation.model.MasterLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterLocationRepo extends JpaRepository<MasterLocation, Long> {

    boolean existsByCityAndStateAndCountry(String city, String state, String country);

    Optional<MasterLocation> findByCityAndStateAndCountry(String city, String state, String country);

    Optional<MasterLocation> findByCityIgnoreCase(String location);
}
