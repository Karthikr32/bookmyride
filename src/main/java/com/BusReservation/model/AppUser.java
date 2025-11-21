package com.BusReservation.model;
import com.BusReservation.constants.Gender;
import com.BusReservation.constants.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class AppUser {    // 13 --> 11

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String password;     // for GUEST -> GUEST_NO_AUTH | USER -> their own password

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean isUser;          // flag to check role before updating | GUEST = false | USER = true | ADMIN = false

    @Column(nullable = false)
    private Boolean isProfileCompleted;

    private LocalDateTime profileUpdatedAt;

    private LocalDateTime registeredAt;

    private LocalDateTime passwordLastUpdatedAt;    // for password update API

    @JsonIgnore
    @OneToMany(mappedBy = "appUser")
    private List<Booking> bookings;
}
