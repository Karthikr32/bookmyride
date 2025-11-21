package com.BusReservation.repository;

import com.BusReservation.constants.*;
import com.BusReservation.model.Booking;
import com.BusReservation.model.MasterLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // this is "JPQL" not native SQL, so, need to write like related to Java fields and classNames. (eg: Booking (className) and bookingStatus (entity_field_name))
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = :bookingStatus1 OR b.bookingStatus = :bookingStatus2")
    List<Booking> findByBookingStatus(@Param("bookingStatus1") BookingStatus bookingStatus1, @Param("bookingStatus2") BookingStatus bookingStatus2);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings WHERE booked_at LIKE CONCAT(:date, '%') OR travel_at = :date OR canceled_at LIKE CONCAT(:date, '%') OR departure_at LIKE CONCAT(:date, '%') OR arrival_at LIKE CONCAT(:date, '%') OR user_edited_at LIKE CONCAT(:date, '%') OR booking_expires_at LIKE CONCAT(:date, '%')")
    Page<Booking> findBookingByAnyDate(@Param("date") String date, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.fromLocation = :location OR b.toLocation = :location")
    Page<Booking> findByLocations(@Param("location") MasterLocation location, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.totalCost = :cost OR b.finalCost = :cost")
    Page<Booking> findByBookingCost(@Param("cost") BigDecimal cost, Pageable pageable);

    Page<Booking> findById(Long bookingId, Pageable pageable);

    Page<Booking> findByBookingStatus(BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    Page<Booking> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    Page<Booking> findByBus_BusType(BusType busType, Pageable pageable);

    Page<Booking> findByBus_AcType(AcType acType, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.bus.seatType LIKE CONCAT('%', :seatType, '%')")
    Page<Booking> findByBus_SeatType(@Param("seatType") String seatType, Pageable pageable);

    Page<Booking> findByBusTicket(String busTicket, Pageable pageable);

    Page<Booking> findByTransactionId(String transactionId, Pageable pageable);

    Page<Booking> findByBus_BusNumber(String busNUmber, Pageable pageable);

    Page<Booking> findByAppUser_Mobile(String mobile, Pageable pageable);       // findByAppUser -> fieldName in Booking to find exact user by mobile use "_" followed by exact field name that declared in AppUser entity

    Page<Booking> findByAppUser_Email(String email, Pageable pageable);

    Page<Booking> findByAppUser_Role(Role role, Pageable pageable);

    Page<Booking> findByAppUser_Gender(Gender gender, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE LOWER(b.bus.busName) LIKE LOWER(CONCAT('%', :busName, '%'))")
    Page<Booking> findByBus_BusName(@Param("busName") String busName, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE LOWER(b.appUser.name) LIKE LOWER(CONCAT('%', :appUserName, '%'))")
    Page<Booking> findByAppUser_Name(@Param("appUserName") String appUserName, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.appUser.id = :userId")
    Page<Booking> getAllBookingData(@Param("userId") Long userId, Pageable pageable);
}
