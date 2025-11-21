package com.BusReservation.repository;
import com.BusReservation.model.Management;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ManagementRepo extends JpaRepository<Management, Long> {

    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);
    boolean existsByEmailOrMobile(String mobile, String email);
    boolean existsByEmailAndMobile(String mobile, String email);
    Optional<Management> findByUsername(String username);
}
