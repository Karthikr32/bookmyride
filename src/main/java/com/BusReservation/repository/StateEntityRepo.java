package com.BusReservation.repository;

import com.BusReservation.constants.State;
import com.BusReservation.model.CountryEntity;
import com.BusReservation.model.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateEntityRepo extends JpaRepository<StateEntity, Long> {
    Optional<StateEntity> findByNameAndCountry(State state, CountryEntity countryEntity);
}
