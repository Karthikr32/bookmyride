package com.BusReservation.repository;

import com.BusReservation.constants.Country;
import com.BusReservation.model.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryEntityRepo extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByName(Country country);
}
