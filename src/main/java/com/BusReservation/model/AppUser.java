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
public class AppUser {

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
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean isUser;

    @Column(nullable = false)
    private Boolean isProfileCompleted;

    private LocalDateTime profileUpdatedAt;

    private LocalDateTime registeredAt;

    private LocalDateTime passwordLastUpdatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "appUser")
    private List<Booking> bookings;
}
