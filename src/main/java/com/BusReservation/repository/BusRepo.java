package com.BusReservation.repository;

import com.BusReservation.constants.AcType;
import com.BusReservation.constants.BookingStatus;
import com.BusReservation.constants.BusType;
import com.BusReservation.constants.State;
import com.BusReservation.dto.BookedBusReportDto;
import com.BusReservation.model.Bus;
import com.BusReservation.model.MasterLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface BusRepo extends JpaRepository<Bus, Long> {

    @Query("SELECT b FROM Bus b JOIN b.bookings bk WHERE bk.bookingStatus = :bookingStatus")
    Page<Bus> findByBookings_BookingStatus(@Param("bookingStatus") BookingStatus bookingStatus, Pageable pageable);

    Page<Bus> findByFare(BigDecimal fare, Pageable pageable);

    Page<Bus> findByAcType(AcType acType, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.seatType LIKE CONCAT('%', :seatType, '%')")
    Page<Bus> findBusDataBySeatType(@Param("seatType") String seatType, Pageable pageable);

    Page<Bus> findByBusType(BusType busType, Pageable pageable);         // can't use enum type with LIKE for partial search bcz LIKE works only for String type

    Page<Bus> findByStateOfRegistration(State state, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM buses WHERE created_at LIKE CONCAT(:date, '%') OR updated_at LIKE CONCAT(:date, '%')")
    Page<Bus> findByCreatedAtOrUpdatedAt(@Param("date") String date, Pageable pageable);              // evenThough denoting 2 fields and passing 1 parameter. But using JPQL/SQL this is valid and work

    boolean existsByBusNumber(String busNumber);

    Page<Bus> findById(Long busId, Pageable pageable);

    Optional<Bus> findByBusNumber(String busNumber);        // passing field as in-sensitive

    Page<Bus> findByBusNumber(String busNumber, Pageable pageable);

    Page<Bus> findByBusName(String busName, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.fromLocation = :location OR b.toLocation = :location")
    Page<Bus> findByLocation(@Param("location") MasterLocation location, Pageable pageable);

    Page<Bus> findByFromLocationAndToLocation(MasterLocation fromLocation, MasterLocation toLocation, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM Buses WHERE departure_at LIKE CONCAT(:time, '%') OR arrival_at LIKE CONCAT(:time, '%')")
    Page<Bus> findByDepartureAtOrArrivalAt(@Param("time") String time, Pageable pageable);

    Page<Bus> findByFromLocationAndToLocationAndAcType(MasterLocation fromLocation, MasterLocation toLocation, AcType acType, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM buses WHERE from_location = :fromLocation AND to_location = :toLocation AND seat_type LIKE CONCAT('%', :seatType, '%')")
    Page<Bus> filterBusByLocationWithSeatType(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("seatType") String seatType, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.fromLocation = :fromLocation AND b.toLocation = :toLocation AND b.acType = :acType AND b.seatType LIKE CONCAT('%', :seatType, '%')")
    Page<Bus> filerBusByLocationWithBothType(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("acType") AcType acType, @Param("seatType") String seatType, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM buses WHERE from_location = :fromLocation AND to_location = :toLocation AND HOUR(departure_at) BETWEEN :startHour AND :endHour")
    Page<Bus> filterBusByTimeRange(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("startHour") Integer startHour, @Param("endHour") Integer endHour, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.fromLocation = :fromLocation AND b.toLocation = :toLocation AND b.acType = :acType AND b.seatType LIKE CONCAT('%', :seatType, '%') AND HOUR(b.departureAt) BETWEEN :startHour AND :endHour")
    Page<Bus> filerBusByLocationWithBothTypeAndTime(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("acType") AcType acType, @Param("seatType") String seatType, @Param("startHour") Integer startHour, @Param("endHour") Integer endHour, Pageable pageable);

    @Query("SELECT b FROM Bus b WHERE b.fromLocation = :fromLocation AND b.toLocation = :toLocation AND b.acType = :acType AND HOUR(b.departureAt) BETWEEN :startHour AND :endHour")
    Page<Bus> filterBusByAcTypeAndTimeRange(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("acType") AcType acType, @Param("startHour") Integer startHour, @Param("endHour") Integer endHour, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM buses WHERE from_location = :fromLocation AND to_location = :toLocation AND seat_type LIKE CONCAT('%', :seatType, '%') AND HOUR(departure_at) BETWEEN :startHour AND :endHour")
    Page<Bus> filterBusBySeatTypeAndTimeRange(@Param("fromLocation") MasterLocation fromLocation, @Param("toLocation") MasterLocation toLocation, @Param("seatType") String seatType, @Param("startHour") Integer startHour, @Param("endHour") Integer endHour, Pageable pageable);

    @Query("SELECT new com.BusReservation.dto.BookedBusReportDto(b.id, b.busNumber, b.busName, b.busType, b.acType, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, FLOOR(((b.capacity - b.availableSeats) * 100) / b.capacity) AS occupancy, FLOOR((b.availableSeats * 100) / b.capacity) AS availability) FROM Bus b JOIN b.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate GROUP BY b.id, b.busNumber, b.busName, b.busType, b.acType, b.capacity, b.availableSeats")
    Page<BookedBusReportDto> findByBookedBusData(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new com.BusReservation.dto.BookedBusReportDto(b.id, b.busNumber, b.busName, b.busType, b.acType, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, FLOOR(((b.capacity - b.availableSeats) * 100) / b.capacity) AS occupancy, FLOOR((b.availableSeats * 100) / b.capacity) AS availability) FROM Bus b JOIN b.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate AND b.acType = :acType GROUP BY b.id, b.busNumber, b.busName, b.busType, b.acType, b.capacity, b.availableSeats")
    Page<BookedBusReportDto> findBookedBusReportByAcType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("acType") AcType acType, Pageable pageable);
}


// busName, busType, location, state, status
// In JPQL & SQL we, can't  use LIKE & BETWEEN together!
// RULE: All selected columns that are not aggregated → MUST appear in GROUP BY.