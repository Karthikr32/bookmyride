package com.BusReservation.mapper;

import com.BusReservation.constants.Country;
import com.BusReservation.constants.State;
import com.BusReservation.dto.LocationResponseDto;
import com.BusReservation.model.Management;
import com.BusReservation.model.CityEntity;
import com.BusReservation.model.CountryEntity;
import com.BusReservation.model.StateEntity;
import java.time.LocalDateTime;

public class LocationMapper {

    public static CountryEntity addDataFromDtoToCountryEntity(Country country, Management management) {
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(country);
        countryEntity.setCreatedById(management);
        countryEntity.setCreatedAt(LocalDateTime.now());
        return countryEntity;
    }


    public static StateEntity addDataFromDtoToStateEntity(State state, CountryEntity countryEntity, Management management) {
        StateEntity stateEntity = new StateEntity();
        stateEntity.setName(state);
        stateEntity.setCountry(countryEntity);
        stateEntity.setCreatedById(management);
        stateEntity.setCreatedAt(LocalDateTime.now());
        return stateEntity;
    }


    public static CityEntity addDataFromDtoToCityEntity(String cityStr, StateEntity stateEntity, Management management) {
        CityEntity city = new CityEntity();
        city.setName(cityStr);
        city.setState(stateEntity);
        city.setCreatedById(management);
        city.setCreatedAt(LocalDateTime.now());
        return city;
    }


    public static LocationResponseDto entityToLocationResponseDto(CityEntity cityEntity) {
        LocationResponseDto locationResponseDto = new LocationResponseDto();
        StateEntity stateEntity = cityEntity.getState();
        CountryEntity countryEntity = stateEntity.getCountry();

        LocationResponseDto.City city = new LocationResponseDto.City();
        city.setId(cityEntity.getId());
        city.setCity(cityEntity.getName());
        city.setCreatedByName(cityEntity.getCreatedById().getUsername());
        city.setCreatedByRole(cityEntity.getCreatedById().getRole().getRoleName());
        city.setCreatedAt(cityEntity.getCreatedAt());
        if(cityEntity.getUpdatedById() == null) {
            city.setUpdatedByName(null);
            city.setUpdatedByRole(null);
            city.setUpdatedAt(null);
        }
        else {
            city.setUpdatedByName(cityEntity.getUpdatedById().getUsername());
            city.setUpdatedByRole(cityEntity.getUpdatedById().getRole().getRoleName());
            city.setUpdatedAt(cityEntity.getUpdatedAt());
        }

        LocationResponseDto.State state = new LocationResponseDto.State();
        state.setId(stateEntity.getId());
        state.setState(stateEntity.getName().getStateName());
        state.setCreatedByName(stateEntity.getCreatedById().getUsername());
        state.setCreatedByRole(stateEntity.getCreatedById().getRole().getRoleName());
        state.setCreatedAt(stateEntity.getCreatedAt());
        if(stateEntity.getUpdatedById() == null) {
            state.setUpdatedByName(null);
            state.setUpdatedByRole(null);
            state.setUpdatedAt(null);
        }
        else {
            state.setUpdatedByName(stateEntity.getUpdatedById().getUsername());
            state.setUpdatedByRole(stateEntity.getUpdatedById().getRole().getRoleName());
            state.setUpdatedAt(stateEntity.getUpdatedAt());
        }

        LocationResponseDto.Country country = new LocationResponseDto.Country();
        country.setId(countryEntity.getId());
        country.setCountry(countryEntity.getName().getCountryName());
        country.setCreatedByName(countryEntity.getCreatedById().getUsername());
        country.setCreatedByRole(countryEntity.getCreatedById().getRole().getRoleName());
        country.setCreatedAt(countryEntity.getCreatedAt());
        if(countryEntity.getUpdatedById() == null) {
            country.setUpdatedByName(null);
            country.setUpdatedByRole(null);
            country.setUpdatedAt(null);
        }
        else {
            country.setUpdatedByName(countryEntity.getUpdatedById().getUsername());
            country.setUpdatedByRole(countryEntity.getUpdatedById().getRole().getRoleName());
            country.setUpdatedAt(countryEntity.getUpdatedAt());
        }

        locationResponseDto.setCity(city);
        locationResponseDto.setCountry(country);
        locationResponseDto.setState(state);
        return locationResponseDto;
    }


    public static CountryEntity updateDataFromDtoToCountryEntity(CountryEntity countryEntity, Country country, Management management) {
        countryEntity.setName(country);
        countryEntity.setUpdatedById(management);
        countryEntity.setUpdatedAt(LocalDateTime.now());
        return countryEntity;
    }


    public static StateEntity updateDataFromDtoToStateEntity(StateEntity stateEntity, State state, CountryEntity countryEntity, Management management) {
        stateEntity.setName(state);
        stateEntity.setCountry(countryEntity);
        stateEntity.setUpdatedById(management);
        stateEntity.setUpdatedAt(LocalDateTime.now());
        return stateEntity;
    }


    public static CityEntity updateDataFromDtoToCityEntity(CityEntity cityEntity, String city, StateEntity stateEntity, Management management) {
        cityEntity.setName(city);
        cityEntity.setState(stateEntity);
        cityEntity.setUpdatedById(management);
        cityEntity.setUpdatedAt(LocalDateTime.now());
        return cityEntity;
    }
}
