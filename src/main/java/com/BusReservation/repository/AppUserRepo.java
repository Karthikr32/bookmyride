package com.BusReservation.repository;

import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import com.BusReservation.dto.BookedAppUserReportDto;
import com.BusReservation.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<AppUser> findByName(@Param("name") String name, Pageable pageable);

    Page<AppUser> findById(Long appUserId, Pageable pageable);

    Page<AppUser> findByMobile(String mobile, Pageable pageable);

    Page<AppUser> findByEmail(String email, Pageable pageable);

    Optional<AppUser> findByMobile(String mobile);

    Page<AppUser> findByRole(Role role, Pageable pageable);

    Page<AppUser> findByGender(Gender gender, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailOrMobile(String email, String mobile);

    @Query("SELECT new com.BusReservation.dto.BookedAppUserReportDto(au.id, au.name, au.email, au.mobile, au.gender, au.role, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, MAX(bk.bookedAt) AS recentBookedAt) FROM AppUser au JOIN au.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate GROUP BY au.id, au.name, au.email, au.mobile, au.gender, au.role")
    Page<BookedAppUserReportDto> findByBookedAppUserData(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new com.BusReservation.dto.BookedAppUserReportDto(au.id, au.name, au.email, au.mobile, au.gender, au.role, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, MAX(bk.bookedAt) AS recentBookedAt) FROM AppUser au JOIN au.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate AND au.gender = :gender AND au.role = :role GROUP BY au.id, au.name, au.email, au.mobile, au.gender, au.role")
    Page<BookedAppUserReportDto> findByBookedAppUserDataByGenderAndRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("gender") Gender gender, @Param("role") Role role, Pageable pageable);

    @Query("SELECT new com.BusReservation.dto.BookedAppUserReportDto(au.id, au.name, au.email, au.mobile, au.gender, au.role, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, MAX(bk.bookedAt) AS recentBookedAt) FROM AppUser au JOIN au.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate AND au.gender = :gender GROUP BY au.id, au.name, au.email, au.mobile, au.gender, au.role")
    Page<BookedAppUserReportDto> findByBookedAppUserDataByGender(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("gender") Gender gender, Pageable pageable);

    @Query("SELECT new com.BusReservation.dto.BookedAppUserReportDto(au.id, au.name, au.email, au.mobile, au.gender, au.role, COUNT(bk.id) AS totalBookings, SUM(bk.finalCost) AS totalRevenue, MAX(bk.bookedAt) AS recentBookedAt) FROM AppUser au JOIN au.bookings bk WHERE bk.bookedAt BETWEEN :startDate AND :endDate AND au.role = :role GROUP BY au.id, au.name, au.email, au.mobile, au.gender, au.role")
    Page<BookedAppUserReportDto> findByBookedAppUserDataByRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("role") Role role, Pageable pageable);
}