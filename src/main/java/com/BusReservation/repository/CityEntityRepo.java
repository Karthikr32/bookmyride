package com.BusReservation.repository;

import com.BusReservation.constants.Country;
import com.BusReservation.constants.Role;
import com.BusReservation.constants.State;
import com.BusReservation.model.CityEntity;
import com.BusReservation.model.StateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CityEntityRepo extends JpaRepository<CityEntity, Long> {

    boolean existsByNameAndState_NameAndState_Country_Name(String city, State state, Country country);

    Page<CityEntity> findByState_Country_Name(Country country, Pageable pageable);

    Page<CityEntity> findByState_Name(State state, Pageable pageable);

    @Query("SELECT c FROM CityEntity c WHERE c.name LIKE CONCAT('%', :city, '%')")
    Page<CityEntity> findByName(@Param("city") String city, Pageable pageable);

    Page<CityEntity> findByCreatedById_Role(Role role, Pageable pageable);

    Page<CityEntity> findByState_NameAndState_Country_Name(State stateEnum, Country countryEnum, Pageable pageable);

    Page<CityEntity> findByNameAndState_NameAndState_Country_Name(String city, State stateEnum, Country countryEnum, Pageable pageable);

    boolean existsByNameAndState(String city, StateEntity state);

    Page<CityEntity> findByNameAndState_Name(String city, State stateEnum, Pageable pageable);
}
