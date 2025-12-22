package com.BusReservation.service;

import com.BusReservation.model.CityEntity;
import com.BusReservation.model.Management;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.repository.MasterLocationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterLocationService {

    private final MasterLocationRepo masterLocationRepo;


    public Optional<MasterLocation> fetchByName(String city, String state, String country) {
        return masterLocationRepo.findByCityAndStateAndCountry(city, state, country);
    }


    public boolean cityStateCountryExists(String city, String state, String country) {
        return masterLocationRepo.existsByCityAndStateAndCountry(city, state, country);
    }


    public void updateMasterLocation(MasterLocation masterLocation, CityEntity cityEntity, Management management) {
        masterLocation.setCity(cityEntity.getName());
        masterLocation.setState(cityEntity.getState().getName().getStateName());
        masterLocation.setCountry(cityEntity.getState().getCountry().getName().getCountryName());
        masterLocation.setUpdatedBy(management);
        masterLocation.setUpdatedAt(LocalDateTime.now());
        masterLocationRepo.save(masterLocation);
    }


    public void saveNewLocationData(CityEntity cityEntity, Management management) {
        MasterLocation masterLocation = new MasterLocation();
        masterLocation.setCity(cityEntity.getName());
        masterLocation.setState(cityEntity.getState().getName().getStateName());
        masterLocation.setCountry(cityEntity.getState().getCountry().getName().getCountryName());
        masterLocation.setCreatedBy(management);
        masterLocation.setCreatedAt(LocalDateTime.now());
        masterLocationRepo.save(masterLocation);
    }


    public void deleteLocationData(MasterLocation masterLocation) {
        masterLocationRepo.delete(masterLocation);
    }


    public void deleteAllLocationData() {
        masterLocationRepo.deleteAll();
    }


    public Optional<MasterLocation> fetchEntityByCity(String location) {
        return masterLocationRepo.findByCityIgnoreCase(location);
    }
}
