package com.BusReservation.service;

import com.BusReservation.constants.Country;
import com.BusReservation.constants.ResponseStatus;
import com.BusReservation.constants.Role;
import com.BusReservation.constants.State;
import com.BusReservation.dto.LocationEntryDto;
import com.BusReservation.dto.LocationResponseDto;
import com.BusReservation.mapper.LocationMapper;
import com.BusReservation.model.*;
import com.BusReservation.repository.CityEntityRepo;
import com.BusReservation.repository.CountryEntityRepo;
import com.BusReservation.repository.StateEntityRepo;
import com.BusReservation.utils.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final CountryEntityRepo countryEntityRepo;
    private final StateEntityRepo stateEntityRepo;
    private final CityEntityRepo cityEntityRepo;
    private final MasterLocationService masterLocationService;


    public ServiceResponse<ApiPageResponse<List<LocationResponseDto>>> getLocationData(PaginationRequest paginationRequest, String country, String state, String city, String role) {
        Pageable pageable = PaginationRequest.getPageable(paginationRequest);
        Integer pageNum = pageable.getPageNumber() + 1;

        if((country != null && !country.isBlank()) && (state != null && !state.isBlank()) && (city != null && !city.isBlank())) {
            if(country.matches(RegExPatterns.COUNTRY_REGEX) && state.matches(RegExPatterns.STATE_REGEX) && city.matches(RegExPatterns.LOCATION_REGEX)) {
                ServiceResponse<Country> countryEnumResponse = ParsingEnumUtils.getParsedEnumType(Country.class, country, "Country");
                ServiceResponse<State> stateEnumResponse = ParsingEnumUtils.getParsedEnumType(State.class, state, "State");
                if(countryEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(countryEnumResponse.getStatus(), countryEnumResponse.getMessage());
                else if(stateEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(stateEnumResponse.getStatus(), stateEnumResponse.getMessage());

                Country countryEnum = countryEnumResponse.getData();
                State stateEnum = stateEnumResponse.getData();

                Page<CityEntity> pageData = cityEntityRepo.findByNameAndState_NameAndState_Country_Name(city, stateEnum, countryEnum, pageable);
                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given country & state & city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given country & state & city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid country/state/city value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if((country != null && !country.isBlank()) && (state != null && !state.isBlank())) {
            if(country.matches(RegExPatterns.COUNTRY_REGEX) && state.matches(RegExPatterns.STATE_REGEX)) {
                ServiceResponse<Country> countryEnumResponse = ParsingEnumUtils.getParsedEnumType(Country.class, country, "Country");
                ServiceResponse<State> stateEnumResponse = ParsingEnumUtils.getParsedEnumType(State.class, state, "State");
                if(countryEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(countryEnumResponse.getStatus(), countryEnumResponse.getMessage());
                else if(stateEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(stateEnumResponse.getStatus(), stateEnumResponse.getMessage());

                Country countryEnum = countryEnumResponse.getData();
                State stateEnum = stateEnumResponse.getData();

                Page<CityEntity> pageData = cityEntityRepo.findByState_NameAndState_Country_Name(stateEnum, countryEnum, pageable);
                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();
                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given country & state input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given country & state input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid country/state value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if((state != null && !state.isBlank()) && (city != null && !city.isBlank())) {
            if(state.matches(RegExPatterns.STATE_REGEX) && city.matches(RegExPatterns.LOCATION_REGEX)) {
                ServiceResponse<State> stateEnumResponse = ParsingEnumUtils.getParsedEnumType(State.class, state, "State");
                if(stateEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(stateEnumResponse.getStatus(), stateEnumResponse.getMessage());
                State stateEnum = stateEnumResponse.getData();

                Page<CityEntity> pageData = cityEntityRepo.findByNameAndState_Name(city, stateEnum, pageable);
                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();
                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given state & city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given state & city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid state/city value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if(country != null && !country.isBlank()) {
            if(country.matches(RegExPatterns.COUNTRY_REGEX)) {
                ServiceResponse<Country> countryEnumResponse = ParsingEnumUtils.getParsedEnumType(Country.class, country, "Country");
                if(countryEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(countryEnumResponse.getStatus(), countryEnumResponse.getMessage());
                Country countryEnum = countryEnumResponse.getData();

                Page<CityEntity> pageData = cityEntityRepo.findByState_Country_Name(countryEnum, pageable);

                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();
                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given country input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given country input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid country value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if(state != null && !state.isBlank()) {
            if(state.matches(RegExPatterns.STATE_REGEX)) {
                ServiceResponse<State> stateEnumResponse = ParsingEnumUtils.getParsedEnumType(State.class, state, "State");
                if(stateEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(stateEnumResponse.getStatus(), stateEnumResponse.getMessage());
                State stateEnum = stateEnumResponse.getData();

                Page<CityEntity> pageData = cityEntityRepo.findByState_Name(stateEnum, pageable);
                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given state input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given state input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid state value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if(city != null && !city.isBlank()) {
            if(city.matches(RegExPatterns.LOCATION_REGEX)) {
                Page<CityEntity> pageData = cityEntityRepo.findByName(city, pageable);
                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given city input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid city value. Each must start with a capital letter. Please correct the input and try again.");
        }
        else if(role != null && !role.isBlank()) {
            if(role.matches(RegExPatterns.ROLE_REGEX)) {
                ServiceResponse<Role> roleEnumResponse = ParsingEnumUtils.getParsedEnumType(Role.class, state, "Role");
                if(roleEnumResponse.getStatus() != ResponseStatus.SUCCESS) return new ServiceResponse<>(roleEnumResponse.getStatus(), roleEnumResponse.getMessage());
                Role roleEnum = roleEnumResponse.getData();
                Page<CityEntity> pageData = cityEntityRepo.findByCreatedById_Role(roleEnum, pageable);

                List<LocationResponseDto> locationResponseDtoList = pageData.stream().map(LocationMapper::entityToLocationResponseDto).toList();
                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found for the given role input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found for the given role input in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(),pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid role input. Please check & retry.");
        }

        Page<CityEntity> cityEntityPage = cityEntityRepo.findAll(pageable);
        List<LocationResponseDto> locationResponseDtoList = cityEntityPage.stream().map(LocationMapper::entityToLocationResponseDto).toList();

        if(cityEntityPage.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No data found in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, cityEntityPage.getTotalPages(), cityEntityPage.getTotalElements(), cityEntityPage.getNumber(), cityEntityPage.getSize(), cityEntityPage.isFirst(), cityEntityPage.isEmpty()));
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Data found in this page " + pageNum, new ApiPageResponse<>(locationResponseDtoList, cityEntityPage.getTotalPages(), cityEntityPage.getTotalElements(), cityEntityPage.getNumber(), cityEntityPage.getSize(), cityEntityPage.isFirst(), cityEntityPage.isEmpty()));
    }


    public boolean cityAndStateAndCountryExists(String city, State state, Country country) {
        return cityEntityRepo.existsByNameAndState_NameAndState_Country_Name(city, state, country);
    }

    public Optional<CountryEntity> countryExistsByName(Country country) {
        return countryEntityRepo.findByName(country);
    }

    public Optional<StateEntity> fetchByStateAndCountryName(State state, CountryEntity countryEntity) {
        return stateEntityRepo.findByNameAndCountry(state, countryEntity);
    }

    public boolean cityAndStateExists(String city, StateEntity state) {
        return cityEntityRepo.existsByNameAndState(city, state);
    }


    @Transactional
    public ServiceResponse<String> postNewLocationData(@Valid LocationEntryDto locationEntryDto, Management management) throws Exception {
        ServiceResponse<Country> parsingCountryResponse = ParsingEnumUtils.getParsedEnumType(Country.class, locationEntryDto.getCountry(), "country");
        if(parsingCountryResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingCountryResponse.getStatus(), parsingCountryResponse.getMessage());
        Country country = parsingCountryResponse.getData();

        ServiceResponse<State> parsingStateResponse = ParsingEnumUtils.getParsedEnumType(State.class, locationEntryDto.getState(), "state");
        if(parsingStateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingStateResponse.getStatus(), parsingStateResponse.getMessage());
        State state = parsingStateResponse.getData();

        boolean isAllExists = this.cityAndStateAndCountryExists(locationEntryDto.getCity(), state, country);
        boolean isAllExistsInMasterLocation = masterLocationService.cityStateCountryExists(locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry());
        if(isAllExists || isAllExistsInMasterLocation)
            return new ServiceResponse<>(ResponseStatus.CONFLICT,
                    "The combination of City: '" + locationEntryDto.getCity() + "', State: '" + locationEntryDto.getState() + "', Country: '" + locationEntryDto.getCountry() + "' already exists. Please provide a different location.");

        try {
            // country
            Optional<CountryEntity> countryOptionalData = this.countryExistsByName(country);
            CountryEntity savedCountryEntity = countryOptionalData.orElseGet(() -> countryEntityRepo.save(LocationMapper.addDataFromDtoToCountryEntity(country, management)));    // if present assign or else map

            // state
            Optional<StateEntity> stateOptionalData = this.fetchByStateAndCountryName(state, savedCountryEntity);
            StateEntity savedStateEntity = stateOptionalData.orElseGet(() -> stateEntityRepo.save(LocationMapper.addDataFromDtoToStateEntity(state, savedCountryEntity, management)));

            // city
            boolean isCityAndStateExists = this.cityAndStateExists(locationEntryDto.getCity(), savedStateEntity);
            if(isCityAndStateExists)
                return new ServiceResponse<>(ResponseStatus.CONFLICT,
                        "City: '" + locationEntryDto.getCity() + "' already exists under State: '" + savedStateEntity.getName().getStateName() + "'. Please provide a different city for this state.");
            CityEntity savedCityEntity = cityEntityRepo.save(LocationMapper.addDataFromDtoToCityEntity(locationEntryDto.getCity(), savedStateEntity, management));
            masterLocationService.saveNewLocationData(savedCityEntity, management);

            log.info("MANAGEMENT ACTION → ID: {} | Username: {} added a new location City: '{}', State: '{}', Country: '{}'  successfully and its ID: {}.", management.getId(), management.getUsername(), locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry(), savedCityEntity.getId());
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Location added successfully by Management Username: '" + management.getUsername() + "'.");
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} attempted to add a location City: '{}', State: '{}', Country: '{}' that was already added by another authority.", management.getId(), management.getUsername(), locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Insertion a new location City: '{}', State: '{}', Country: '{}' was been failed due to {}", e.getMessage(), locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry());
            throw e;
        }
    }


    @Transactional
    public ServiceResponse<List<String>> postListOfNewLocationData(@Valid List<LocationEntryDto> locationEntryDtoList, Management management) {
        List<String> results = new ArrayList<>();  // conflicts, success, error

        for(LocationEntryDto location : locationEntryDtoList) {
            ServiceResponse<Country> parsingCountryResponse = ParsingEnumUtils.getParsedEnumType(Country.class, location.getCountry(), "country");
            if(parsingCountryResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingCountryResponse.getStatus(), "City: '" + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' Was failed due to " + parsingCountryResponse.getMessage());
            Country country = parsingCountryResponse.getData();

            ServiceResponse<State> parsingStateResponse = ParsingEnumUtils.getParsedEnumType(State.class, location.getState(), "state");
            if(parsingStateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingStateResponse.getStatus(), "City: '" + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' Was failed due to " + parsingStateResponse.getMessage());
            State state = parsingStateResponse.getData();

            boolean isExists = cityEntityRepo.existsByNameAndState_NameAndState_Country_Name(location.getCity(), state, country);
            boolean isAllExistsInMasterLocation = masterLocationService.cityStateCountryExists(location.getCity(), location.getState(), location.getCountry());
            if(isExists || isAllExistsInMasterLocation) {
                results.add("DUPLICATE_ENTRY: The combination of City: '" + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' already exists. Please provide a different location.");
                continue;
            }

            try {
                // country
                Optional<CountryEntity> countryEntityOptional = this.countryExistsByName(country);
                CountryEntity savedCountryEntity = countryEntityOptional.orElseGet(() -> countryEntityRepo.save(LocationMapper.addDataFromDtoToCountryEntity(country, management)));

                // state
                Optional<StateEntity> stateEntityOptional = this.fetchByStateAndCountryName(state, savedCountryEntity);
                StateEntity savedStateEntity = stateEntityOptional.orElseGet(() -> stateEntityRepo.save(LocationMapper.addDataFromDtoToStateEntity(state, savedCountryEntity, management)));

                // city
                boolean isCityAndStateExists = this.cityAndStateExists(location.getCity(), savedStateEntity);
                if(isCityAndStateExists) {
                    String error = "DUPLICATE_ENTRY: City: '" + location.getCity() + "' already exists under State: '" + savedStateEntity.getName().getStateName() + "'. Please provide a different city for this state.";
                    results.add(error);
                    continue;
                }
                CityEntity savedCityEntity = cityEntityRepo.save(LocationMapper.addDataFromDtoToCityEntity(location.getCity(), savedStateEntity, management));
                masterLocationService.saveNewLocationData(savedCityEntity, management);
                results.add("SUCCESS: The combination of City: '" + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' was added successfully with an ID: " + savedCityEntity.getId());
            }
            catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                results.add("DUPLICATE_ENTRY: " + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' was already added by another management authority.");
                log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} was made an attempt to add City: '{}', State: '{}', Country: '{}' that were recently added by another management  authority.", management.getId(), management.getUsername(), location.getCity(), location.getState(), location.getCountry());
            }
            catch (Exception e) {
                results.add("ERROR: "  + location.getCity() + "', State: '" + location.getState() + "', Country: '" + location.getCountry() + "' was failed to insert due to internal server error.");
                log.warn("ERROR -> Insertion new City: '{}', State: '{}', Country: '{}' was failed due to {}", e.getMessage(), location.getCity(), location.getState(), location.getCountry());
            }
        }

        Long successCount = results.stream().filter(list -> list.startsWith("SUCCESS:")).count();
        Long failureCount = results.size() - successCount;

        if(successCount == locationEntryDtoList.size()) {
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Added " + locationEntryDtoList.size() + " new locations and saved successfully by Management Username: '" + management.getUsername() + "'.");
        }
        return new ServiceResponse<>(ResponseStatus.CONFLICT,
                "Saved " + successCount + " new locations successfully by Management Username: '" + management.getUsername() + "'." + " Skipped " + failureCount + " due to duplicate entry. Here's the skipped insertion List to view.", results.stream().filter(data -> !data.startsWith("SUCCESS")).toList());
    }


    @Transactional
    public ServiceResponse<String> deleteLocationDataById(Long id, Management management) {
        Optional<CityEntity> cityEntityOptional = cityEntityRepo.findById(id);
        if(cityEntityOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No location record exists for ID: " + id);

        CityEntity cityEntity = cityEntityOptional.get();
        StateEntity stateEntity = cityEntity.getState();
        CountryEntity countryEntity = stateEntity.getCountry();
        try {
            Optional<MasterLocation> masterLocationOptional = masterLocationService.fetchByName(cityEntity.getName(), stateEntity.getName().getStateName(), countryEntity.getName().getCountryName());
            if(masterLocationOptional.isEmpty()) {
                log.warn("MASTER_LOCATION: Location with City: {}, State: {}, Country: {} were not found in Master Location Database in order to delete.", cityEntity.getName(), stateEntity.getName().getStateName(), countryEntity.getName().getCountryName());
                return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No location record exists in MasterLocation for given City ID: " + id);
            }

            cityEntityRepo.delete(cityEntity);
            masterLocationService.deleteLocationData(masterLocationOptional.get());

            log.info("MANAGEMENT ACTION -> ID: {} | Username: {} deleted a location with City: '{}', State: '{}', Country: '{}' successfully with ID: {}", management.getId(), management.getUsername(), cityEntity.getName(), stateEntity.getName().getStateName(), countryEntity.getName().getCountryName(), id);
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Location Id " + id + " was deleted successfully by Management Username: '" + management.getUsername() + "'.");
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} attempted to delete a location data that were recently modified by another management authority.", management.getId(), management.getUsername());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Location deletion failed due to {}", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public ServiceResponse<String> clearAllLocationData(Management management) {
        try {
            cityEntityRepo.deleteAll();
            stateEntityRepo.deleteAll();
            countryEntityRepo.deleteAll();

            masterLocationService.deleteAllLocationData();
            log.info("MANAGEMENT ACTION -> ID: {} | Username: {} deleted all location data's successfully", management.getId(), management.getUsername());
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "All records deleted successfully by Management Username: '" + management.getUsername() + "'.");
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} attempted to delete whole location details that were recently deleted by another management authority.", management.getId(), management.getUsername());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Clear all location failed due to {}", e.getMessage());
            throw e;
        }
    }


    @Transactional
    public ServiceResponse<String> updateLocationData(Long id, @Valid LocationEntryDto locationEntryDto, Management management) {
        Optional<CityEntity> cityEntityOptional = cityEntityRepo.findById(id);
        if(cityEntityOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No location record exists for ID: " + id);

        ServiceResponse<Country> parsingCountryResponse = ParsingEnumUtils.getParsedEnumType(Country.class, locationEntryDto.getCountry(), "country");
        if(parsingCountryResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingCountryResponse.getStatus(), parsingCountryResponse.getMessage());
        Country country = parsingCountryResponse.getData();

        ServiceResponse<State> parsingStateResponse = ParsingEnumUtils.getParsedEnumType(State.class, locationEntryDto.getState(), "state");
        if(parsingStateResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsingStateResponse.getStatus(), parsingStateResponse.getMessage());
        State state = parsingStateResponse.getData();

        CityEntity cityEntity = cityEntityOptional.get();
        StateEntity stateEntity = cityEntity.getState();
        CountryEntity countryEntity = stateEntity.getCountry();

        boolean isAllExists = this.cityAndStateAndCountryExists(locationEntryDto.getCity(), state, country);
        boolean isAllExistsInMasterLocation = masterLocationService.cityStateCountryExists(locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry());
        if(isAllExists || isAllExistsInMasterLocation) {
            if(!cityEntity.getName().equalsIgnoreCase(locationEntryDto.getCity()) || stateEntity.getName() != state || countryEntity.getName() != country)
                return new ServiceResponse<>(ResponseStatus.CONFLICT,
                    "The combination of City: '" + locationEntryDto.getCity() + "', State: '" + locationEntryDto.getState() + "', Country: '" + locationEntryDto.getCountry() + "' already exists. Please provide a different location.");
        }

        Optional<MasterLocation> masterLocationOptional = masterLocationService.fetchByName(cityEntity.getName(), stateEntity.getName().getStateName(), countryEntity.getName().getCountryName());
        if(masterLocationOptional.isEmpty()) {
            log.warn("DATA INCONSISTENCY → MasterLocation entry missing for: City: '{}', State: '{}', Country: '{}' but this data would present in City/State/Country Entities.", cityEntity.getName(), stateEntity.getName().getStateName(), countryEntity.getName().getCountryName());
            return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "The corresponding MasterLocation record for this city, state, and country could not be found. This indicates a data inconsistency.");
        }

        try {
            // country
            CountryEntity updatedCountryEntity = (countryEntity.getName() != country) ? countryEntityRepo.save(LocationMapper.updateDataFromDtoToCountryEntity(countryEntity, country, management)) : countryEntity;

            // state
            StateEntity updatedStateEntity = (stateEntity.getName() != state) ? stateEntityRepo.save(LocationMapper.updateDataFromDtoToStateEntity(stateEntity, state, updatedCountryEntity, management)) : stateEntity;

            // city
            boolean cityAndStateExists = this.cityAndStateExists(locationEntryDto.getCity(), updatedStateEntity);
            if(cityAndStateExists && !cityEntity.getName().equalsIgnoreCase(locationEntryDto.getCity()))
                return new ServiceResponse<>(ResponseStatus.CONFLICT,
                        "The city '" + locationEntryDto.getCity() + "' already exists under the state '" + updatedStateEntity.getName().getStateName() + "'. Please provide a different city name for this state.");

            CityEntity updatedCityEntity = (!cityEntity.getName().equalsIgnoreCase(locationEntryDto.getCity())) ? cityEntityRepo.save(LocationMapper.updateDataFromDtoToCityEntity(cityEntity, locationEntryDto.getCity(), updatedStateEntity, management)) : cityEntity;
            masterLocationService.updateMasterLocation(masterLocationOptional.get(), updatedCityEntity, management);

            log.info("MANAGEMENT ACTION → ID: {} | Username: {} updated a location to City: '{}', State: '{}', Country: '{}' successfully for Location ID: {}.", management.getId(), management.getUsername(), locationEntryDto.getCity(), locationEntryDto.getState(), locationEntryDto.getCountry(), id);
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Location updated successfully by Management Username: '" + management.getUsername() + "'.");
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} attempted to update location details that were recently modified by another management authority.", management.getId(), management.getUsername());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Location update failed due to {}", e.getMessage());
            throw e;
        }
    }
}


