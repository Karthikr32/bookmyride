package com.BusReservation.service;

import com.BusReservation.constants.*;
import com.BusReservation.dto.ManagementBusDataDto;
import com.BusReservation.dto.BusDto;
import com.BusReservation.dto.BusUserResponseDto;
import com.BusReservation.dto.BookedBusReportDto;
import com.BusReservation.mapper.BusMapper;
import com.BusReservation.model.Management;
import com.BusReservation.model.Bus;
import com.BusReservation.model.MasterLocation;
import com.BusReservation.repository.BusRepo;
import com.BusReservation.utils.*;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepo busRepo;
    private final MasterLocationService masterLocationService;


    public ServiceResponse<ApiPageResponse<List<ManagementBusDataDto>>> getAllBusInfo(PaginationRequest request, String keyword) {
        Pageable pageable = PaginationRequest.getPageable(request);
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword == null || keyword.isBlank()) {
            Page<Bus> pageData = busRepo.findAll(pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The available Bus data list in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }

        if(keyword.startsWith("id_")) {    // 3
            Long busId = Long.parseLong(keyword.substring(3));
            if(busId <= 0) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Bus ID. ID cannot be 0 or less than 0");

            Page<Bus> pageData = busRepo.findById(busId, pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus ID "+ busId + " in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Bus data found for the given Bus ID in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("fare_")) {    // can accept values like 123.00 or 123 or 123.99
            String costStr = keyword.trim().substring(5);

            BigDecimal fareCost;
            if(costStr.matches(RegExPatterns.COST_WITH_DECIMAL_REGEX)) {
                fareCost = BigDecimal.valueOf(Double.parseDouble(costStr));
            }
            else if (costStr.matches(RegExPatterns.COST_WITHOUT_DECIMAL_REGEX)) {
                fareCost = BigDecimal.valueOf(Long.parseLong(costStr));
            }
            else return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid fare input. Please enter a valid amount like 123, 123.00, or 123.99.");

            Page<Bus> pageData = busRepo.findByFare(fareCost, pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Fare in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Bus data found for the given Bus Fare in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("bus_")) {    // for busName
            String busName = keyword.substring(4);
            if(!busName.matches(RegExPatterns.BUS_NAME_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Bus name input. Bus name must start with a capital letter and can include letters, numbers, spaces, and symbols like @, (), /, &, .");

            Page<Bus> pageData = busRepo.findByBusName(busName, pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Name in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Bus Name in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.startsWith("location_")) {    // 9
            String location = keyword.substring(9).trim();

            if(!location.matches(RegExPatterns.LOCATION_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid City Name. Only letters, spaces, and hyphens are allowed. No numbers or special characters.");
            Optional<MasterLocation> masterLocationOptional = masterLocationService.fetchEntityByCity(location);
            if(masterLocationOptional.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Given location '" + location + "' is not found in MasterLocation table.");

            Page<Bus> pageData = busRepo.findByLocation(masterLocationOptional.get(), pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Location in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Location in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if (keyword.matches(RegExPatterns.BUS_NUMBER_REGEX)) {
            Page<Bus> pageData = busRepo.findByBusNumber(keyword, pageable);

            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();
            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Number "+ keyword + " in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "The Bus data found for the given Bus Number in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if (keyword.matches(RegExPatterns.TIME_REGEX)) {
            try {
                DateTimeFormatter formatter = (keyword.length() == 5) ? DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
                LocalTime busTime = LocalTime.parse(keyword, formatter);
                Page<Bus> pageData = busRepo.findByDepartureAtOrArrivalAt(busTime.toString(), pageable);

                List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();
                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Time in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "The available Bus data for the given Bus Time in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            catch (DateTimeParseException e) {
                return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Time input, Please ensure an correct format (HH:mm)");
            }
        }       // 01/01/2025 | 1-1-2025 OR 2025-01-01
        else if(keyword.matches(RegExPatterns.DATE_REGEX)) {
            DateTimeFormatter format = keyword.contains("-") ? DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT) : DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

            ServiceResponse<LocalDate> parsedDate = DateParser.parseDate(keyword, format);
            if(parsedDate.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedDate.getStatus(), parsedDate.getMessage());

            String dateStr = parsedDate.getData().toString();
            ServiceResponse<Page<Bus>> response = this.getBusDataByDate(dateStr, pageable);

            Page<Bus> pageData =  response.getData();
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(response.getStatus() == ResponseStatus.NOT_FOUND) return new ServiceResponse<>(response.getStatus(), response.getMessage(), new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(response.getStatus(), response.getMessage(), new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        return this.getBusDataByMatchedKeyword(keyword, pageable);
    }

    public boolean existsBusNumber(String busNumber) {
         return busRepo.existsByBusNumber(busNumber.trim());
    }


    public ServiceResponse<String> addNewBusInfoData(Management management, BusDto busDto) {
        try {
            ServiceResponse<BusType> parsedBusTypeEnum = ParsingEnumUtils.getParsedEnumType(BusType.class, busDto.getBusType(), "Bus type");
            ServiceResponse<State> parsedStateEnum = ParsingEnumUtils.getParsedEnumType(State.class, busDto.getStateOfRegistration(), "State");
            ServiceResponse<PermitStatus> parsedPermitStatusEnum = ParsingEnumUtils.getParsedEnumType(PermitStatus.class, busDto.getInterStatePermitStatus(), "Permit status");
            ServiceResponse<Map<String, MasterLocation>> locationResponse = ValidateLocationUtils.validateLocation(busDto.getFromLocation(), busDto.getToLocation(), "Management", masterLocationService);

            if(parsedBusTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedBusTypeEnum.getStatus(), parsedBusTypeEnum.getMessage());
            else if(parsedStateEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedStateEnum.getStatus(), parsedStateEnum.getMessage());
            else if(parsedPermitStatusEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedPermitStatusEnum.getStatus(), parsedPermitStatusEnum.getMessage());
            else if(locationResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(locationResponse.getStatus(), locationResponse.getMessage());
            else if(locationResponse.getStatus() == ResponseStatus.NOT_FOUND) return new ServiceResponse<>(locationResponse.getStatus(), locationResponse.getMessage());

            BusType busType = parsedBusTypeEnum.getData();
            State state = parsedStateEnum.getData();
            PermitStatus permitStatus = parsedPermitStatusEnum.getData();
            MasterLocation fromMasterLocation = locationResponse.getData().get("from");
            MasterLocation toMasterLocation = locationResponse.getData().get("to");

            LocalTime departureAt;
            try {
                departureAt = LocalTime.parse(busDto.getDepartureAt(), DateTimeFormatter.ofPattern("HH:mm:ss").withResolverStyle(ResolverStyle.STRICT));
            }
            catch (DateTimeParseException e) {
                return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid time input. Departure at must be in (HH:mm:ss) format.");
            }

            Bus savedBus = busRepo.save(BusMapper.toEntity(busDto, busType, state, permitStatus, fromMasterLocation, toMasterLocation, departureAt, management));
            log.info("MANAGEMENT ACTION → ID: {} | Username: {} added a new bus successfully with ID: {}.", management.getId(), management.getUsername(), savedBus.getId());
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus ID " + savedBus.getId() + " was added successfully by Management Username: '" + management.getUsername() + "'.");
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management ID: {} | Username: {} attempted to add a bus that were recently added by another management authority.", management.getId(), management.getUsername());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Insertion failed: {}", e.getMessage());
            throw e;
        }
    }


    public ServiceResponse<String> updateExistingBusInfo(Long id, BusDto busDto, Management management) {
       Optional<Bus> existingBus = busRepo.findById(id);
       if(existingBus.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Bus ID " + id + " is not existed in database.");
       Bus bus = existingBus.get();

       try {
           ServiceResponse<BusType> parsedBusTypeEnum = ParsingEnumUtils.getParsedEnumType(BusType.class, busDto.getBusType(), "Bus type");
           ServiceResponse<State> parsedStateEnum = ParsingEnumUtils.getParsedEnumType(State.class, busDto.getStateOfRegistration(), "State");
           ServiceResponse<PermitStatus> parsedPermitStatusEnum = ParsingEnumUtils.getParsedEnumType(PermitStatus.class, busDto.getInterStatePermitStatus(), "Permit status");
           ServiceResponse<Map<String, MasterLocation>> locationResponse = ValidateLocationUtils.validateLocation(busDto.getFromLocation(), busDto.getToLocation(), "Management", masterLocationService);

           if(parsedBusTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedBusTypeEnum.getStatus(), parsedBusTypeEnum.getMessage());
           else if(parsedStateEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedStateEnum.getStatus(), parsedStateEnum.getMessage());
           else if(parsedPermitStatusEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedPermitStatusEnum.getStatus(), parsedPermitStatusEnum.getMessage());
           else if(locationResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(locationResponse.getStatus(), locationResponse.getMessage());
           else if(locationResponse.getStatus() == ResponseStatus.NOT_FOUND) return new ServiceResponse<>(locationResponse.getStatus(), locationResponse.getMessage());

           BusType busType = parsedBusTypeEnum.getData();
           State state = parsedStateEnum.getData();
           PermitStatus permitStatus = parsedPermitStatusEnum.getData();
           MasterLocation fromMasterLocation = locationResponse.getData().get("from");
           MasterLocation toMasterLocation = locationResponse.getData().get("to");
           LocalTime departureAt;
           try {
               departureAt = LocalTime.parse(busDto.getDepartureAt(), DateTimeFormatter.ofPattern("HH:mm:ss").withResolverStyle(ResolverStyle.STRICT));
           }
           catch (DateTimeParseException e) {
               return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid time input. Departure at must be in (HH:mm:ss) format.");
           }

           Bus savedBus = busRepo.save(BusMapper.updateExistingByDto(bus, busDto, busType, state, permitStatus, fromMasterLocation, toMasterLocation, departureAt, management));
           log.info("MANAGEMENT ACTION → ID: {} | Username: {} modified a bus successfully with ID: {}.", management.getId(), management.getUsername(), bus.getId());
           return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus ID " + savedBus.getId() + " was added successfully by Management Username: '" + management.getUsername() + "'.");
       }
       catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
           log.info("OPTIMISTIC LOCK -> Management   ID: {} | Username: {} attempted to modify a bus that were recently modified by another management authority.", management.getId(), management.getUsername());
           throw e;
       }
       catch (Exception e) {
           log.warn("INTERNAL ERROR -> Updation failed: {}", e.getMessage());
           throw e;
       }
    }


    public ServiceResponse<String> deleteByBusId(Long id, Management management) {  // 2 Db checks is important
        Optional<Bus> existingBus = busRepo.findById(id);
        if(existingBus.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "Bus ID " + id + " is not existed in database.");
        Bus bus = existingBus.get();

        try {
            busRepo.delete(bus);        // using .deleteById() will no longer hold version value, its just hold ID, in order to maintain proper deletion using @Version field, need to use .delete();
            log.info("MANAGEMENT ACTION -> ID: {} | Username: {} deleted a bus data successfully with ID: {}", management.getId(), management.getUsername(), id);
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus ID " + bus.getId() + " was deleted successfully by Management Username: " + management.getUsername());   // if success
        }
        catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            log.info("OPTIMISTIC LOCK -> Management   ID: {} | Username: {} attempted to delete a bus that were recently deleted by another management authority.", management.getId(), management.getUsername());
            throw e;
        }
        catch (Exception e) {
            log.warn("INTERNAL ERROR -> Deletion failed: {}", e.getMessage());
            throw e;
        }
    }


    public Optional<Bus> fetchByBusNumber(String busNumber) {
        return busRepo.findByBusNumber(busNumber.trim());
    }


    public ServiceResponse<List<BusUserResponseDto>> searchBusByUserRequest(String fromLocation, String toLocation, LocalDate travelDate, PaginationRequest request, String acType, String seatType, String timeRange) {
        Pageable pageable = PaginationRequest.getPageable(request);
        ServiceResponse<Map<String, MasterLocation>> masterLocationResponse = ValidateLocationUtils.validateLocation(fromLocation, toLocation, "User", masterLocationService);

        if(masterLocationResponse.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(masterLocationResponse.getStatus(), masterLocationResponse.getMessage());
        else if(masterLocationResponse.getStatus() == ResponseStatus.NOT_FOUND) return new ServiceResponse<>(masterLocationResponse.getStatus(), masterLocationResponse.getMessage());
        MasterLocation fromMasterLocation = masterLocationResponse.getData().get("from");
        MasterLocation toMasterLocation = masterLocationResponse.getData().get("to");

        // busType && seatType && timeRange
        if((acType != null && !acType.isBlank()) && (seatType != null && !seatType.isBlank()) && (timeRange != null && !timeRange.isBlank())) {
            if(acType.matches(RegExPatterns.AC_TYPE_REGEX) && seatType.matches(RegExPatterns.SEAT_TYPE_REGEX) && timeRange.matches(RegExPatterns.TIME_RANGE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, acType, "Ac Type");
                ServiceResponse<Integer[]> parsedTime = TimeRangeParser.timeRangeParser(timeRange);

                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());
                else if(parsedTime.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedTime.getStatus(), parsedTime.getMessage());
                Integer[] time = parsedTime.getData();

                Page<Bus> pageData = busRepo.filerBusByLocationWithBothTypeAndTime(fromMasterLocation, toMasterLocation, acTypeEnum.getData(), NormalizeStringUtils.getNormalize(seatType), time[0], time[1], pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given AC & Seat Type and Departure Time.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given AC & Seat Type and Departure Time", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input values. Please use valid AC Type (AC/NON-AC), Seat Type (SEATER/SLEEPER), and Time Range in HH-HH format.");
        }   // busType && seatType
        if((acType != null && !acType.isBlank()) && (seatType != null && !seatType.isBlank())) {
            if(acType.matches(RegExPatterns.AC_TYPE_REGEX) && seatType.matches(RegExPatterns.SEAT_TYPE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, acType, "Ac Type");
                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());

                Page<Bus> pageData = busRepo.filerBusByLocationWithBothType(fromMasterLocation, toMasterLocation, acTypeEnum.getData(), NormalizeStringUtils.getNormalize(seatType), pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given AC and Seat Type.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given AC and Seat Type.", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid AC Type or Seat Type. Accepted values: Bus Type - AC or NON-AC, Seat Type - SEATER or SLEEPER.");
        }  // busType && timeRange
        if((acType != null && !acType.isBlank()) && (timeRange != null && !timeRange.isBlank())) {
            if(acType.matches(RegExPatterns.AC_TYPE_REGEX) && timeRange.matches(RegExPatterns.TIME_RANGE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, acType, "Ac Type");
                ServiceResponse<Integer[]> parsedTime = TimeRangeParser.timeRangeParser(timeRange);

                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());
                else if(parsedTime.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedTime.getStatus(), parsedTime.getMessage());
                Integer[] time = parsedTime.getData();

                Page<Bus> pageData = busRepo.filterBusByAcTypeAndTimeRange(fromMasterLocation, toMasterLocation, acTypeEnum.getData(), time[0], time[1], pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given AC Type and Departure Time.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given AC Type and Departure Time", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Bus Type or Time Range format. Use Bus Type - AC or NON-AC and Time Range in HH-HH format.");
        }  // seatType && timeRange
        if((seatType != null && !seatType.isBlank()) && (timeRange != null && !timeRange.isBlank())) {
            if(seatType.matches(RegExPatterns.SEAT_TYPE_REGEX) && timeRange.matches(RegExPatterns.TIME_RANGE_REGEX)) {
                ServiceResponse<Integer[]> parsedTime = TimeRangeParser.timeRangeParser(timeRange);
                if(parsedTime.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedTime.getStatus(), parsedTime.getMessage());
                Integer[] time = parsedTime.getData();

                Page<Bus> pageData = busRepo.filterBusBySeatTypeAndTimeRange(fromMasterLocation, toMasterLocation, NormalizeStringUtils.getNormalize(seatType), time[0], time[1], pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given Seat Type and Departure Time.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Seat Type and Departure Time", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid Seat Type or Time Range format. Seat Type must be SEATER or SLEEPER. Time Range must be in HH-HH format.");
        }   // busType
        if(acType != null && !acType.isBlank()) {
            if(acType.matches(RegExPatterns.AC_TYPE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, acType, "Ac Type");
                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());

                Page<Bus> pageData = busRepo.findByFromLocationAndToLocationAndAcType(fromMasterLocation, toMasterLocation, acTypeEnum.getData(), pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given AC Type.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given AC Type", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input values. Please use valid Bus Type (AC/NON-AC).");

        }  // seatType
        if(seatType != null && !seatType.isBlank()) {
            if(seatType.matches(RegExPatterns.SEAT_TYPE_REGEX)) {
                Page<Bus> pageData = busRepo.filterBusByLocationWithSeatType(fromMasterLocation, toMasterLocation, NormalizeStringUtils.getNormalize(seatType), pageable);
                List<Bus> busList = pageData.getContent();

                if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given Seat Type.");
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Seat Type.", BusMapper.butToBusUserDto(busList, travelDate));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid input values. Please use valid Seat Type (SEATER/SLEEPER).");
        }    // timeRange
        if(timeRange != null && !timeRange.isBlank()) {
            if(!timeRange.matches(RegExPatterns.TIME_RANGE_REGEX)) return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid time range format. Please provide time in HH-HH format (e.g., 06-12).");

            ServiceResponse<Integer[]> parsedTime = TimeRangeParser.timeRangeParser(timeRange);
            if(parsedTime.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(parsedTime.getStatus(), parsedTime.getMessage());
            Integer[] time = parsedTime.getData();

            Page<Bus> pageData = busRepo.filterBusByTimeRange(fromMasterLocation, toMasterLocation, time[0], time[1], pageable);
            List<Bus> busList = pageData.getContent();

            if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus found for the given Departure time.");
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Departure time.", BusMapper.butToBusUserDto(busList, travelDate));
        }

        Page<Bus> pageData = busRepo.findByFromLocationAndToLocation(fromMasterLocation, toMasterLocation, pageable);
        List<Bus> busList = pageData.getContent();

        if(busList.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No bus data found for the given inputs.");
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given locations.", BusMapper.butToBusUserDto(busList, travelDate));
    }


    public ServiceResponse<Page<Bus>> getBusDataByDate(String date, Pageable pageable) {
        Integer pageNum = pageable.getPageNumber() + 1;
        Page<Bus> data = busRepo.findByCreatedAtOrUpdatedAt(date, pageable);

        if(data.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Date in this page " + pageNum, data);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "The available Bus data for the given Date in this page " + pageNum, data);
    }

    public ServiceResponse<ApiPageResponse<List<ManagementBusDataDto>>> getBusDataByMatchedKeyword(String keyword, Pageable pageable) {
        Integer pageNum = pageable.getPageNumber() + 1;

        if(keyword.matches(RegExPatterns.BOOKING_STATUS_REGEX)) {
            ServiceResponse<BookingStatus> bookingStatusEnum = ParsingEnumUtils.getParsedEnumType(BookingStatus.class, keyword, "Booking Status");
            if(bookingStatusEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(bookingStatusEnum.getStatus(), bookingStatusEnum.getMessage());

            Page<Bus> pageData = busRepo.findByBookings_BookingStatus(bookingStatusEnum.getData(), pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Booking Status in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Bus Booking Status in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BUS_TYPE_REGEX)) {
            ServiceResponse<BusType> busTypeEnum = ParsingEnumUtils.getParsedEnumType(BusType.class, keyword, "Bus Type");
            if(busTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(busTypeEnum.getStatus(), busTypeEnum.getMessage());

            Page<Bus> pageData = busRepo.findByBusType(busTypeEnum.getData(), pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.STATE_REGEX)) {
            ServiceResponse<State> stateEnum = ParsingEnumUtils.getParsedEnumType(State.class, keyword, "State");
            if(stateEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(stateEnum.getStatus(), stateEnum.getMessage());

            Page<Bus> pageData = busRepo.findByStateOfRegistration(stateEnum.getData(), pageable);
            List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

            if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given State in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given State in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        }
        else if(keyword.matches(RegExPatterns.BASIC_BUS_TYPE_REGEX)) {
            if(keyword.matches(RegExPatterns.AC_TYPE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, keyword, "Ac Type");
                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());

                Page<Bus> pageData = busRepo.findByAcType(acTypeEnum.getData(), pageable);

                List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            else if(keyword.matches(RegExPatterns.SEAT_TYPE_REGEX)) {
                Page<Bus> pageData = busRepo.findBusDataBySeatType(NormalizeStringUtils.getNormalize(keyword), pageable);
                List<ManagementBusDataDto> busList = pageData.stream().map(BusMapper::busToManagementBusDataDto).toList();

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given Bus Type in this page " + pageNum, new ApiPageResponse<>(busList, pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid filter keyword for Bus Type. Please use valid options such as AC, Non AC, Sleeper, or Seater.");
        }
        return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data found for the given Input in this page " + pageNum, new ApiPageResponse<>(List.of(), 0, 0L, pageable.getPageNumber(), pageable.getPageSize(), true, true));
    }

    public ServiceResponse<ApiPageResponse<List<BookedBusReportDto>>> getBookedBusReport(LocalDateTime startDate, LocalDateTime endDate, PaginationRequest request, String category) {
        Pageable pageable = PaginationRequest.getPageable(request);
        Integer pageNum = pageable.getPageNumber() + 1;

        if(category != null && !category.isBlank()) {
            if(category.matches(RegExPatterns.AC_TYPE_REGEX)) {
                ServiceResponse<AcType> acTypeEnum = ParsingEnumUtils.getParsedEnumType(AcType.class, category, "Ac Type");
                if(acTypeEnum.getStatus() == ResponseStatus.BAD_REQUEST) return new ServiceResponse<>(acTypeEnum.getStatus(), acTypeEnum.getMessage());

                Page<BookedBusReportDto> pageData = busRepo.findBookedBusReportByAcType(startDate, endDate, acTypeEnum.getData(), pageable);

                if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data for the given category in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
                return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given category in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
            }
            return new ServiceResponse<>(ResponseStatus.BAD_REQUEST, "Invalid category inout. Category only allowed for AC or Non AC.");
        }

        Page<BookedBusReportDto> pageData = busRepo.findByBookedBusData(startDate, endDate, pageable);

        if(pageData.isEmpty()) return new ServiceResponse<>(ResponseStatus.NOT_FOUND, "No Bus data for the given date in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Bus data found for the given date in this page " + pageNum, new ApiPageResponse<>(pageData.getContent(), pageData.getTotalPages(), pageData.getTotalElements(), pageData.getNumber(), pageData.getSize(), pageData.isFirst(), pageData.isEmpty()));
    }
}