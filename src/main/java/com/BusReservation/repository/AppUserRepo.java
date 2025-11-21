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

/* NOTE
 -> why nativeQuery for finding name instead not like other method, bcz also can use findByName(String name) without any Query. But it results exact match, in my case I want the match either by first name or last name.
 -> This JPA native query "SELECT * FROM customer WHERE name LIKE CONCAT(:name, '%')"
 is Exactly like "SELECT * FROM customer WHERE name LIKE 'ram%';". Even it works for single character too.

 -> In JPA we, binding the Java Object(variable value) to query value (this must be variable type, not to put inside "") also LIKE as to use "%" this must be in String type. So using CONCAT()
 -> In the findByNameIgnoreCase() method after @Param. Had used String name indicates the variable that holds Java Object received via Get Request, This used to bind Method parameter to Query parameter.
*/
