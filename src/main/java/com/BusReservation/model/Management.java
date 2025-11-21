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

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "management")
public class Management {      // 11  --> 10

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;         // can allow single/double name

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true, nullable = false)
    private String username;        // will be generated via backend

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime profileUpdatedAt;      // for profile update API

    private LocalDateTime passwordLastUpdatedAt;   // for password update API

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy")
    private List<Bus> createdBusList;

    @JsonIgnore
    @OneToMany(mappedBy = "updatedBy")
    private List<Bus> updatedBusList;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy")
    private List<MasterLocation> createdLocationList;

    @JsonIgnore
    @OneToMany(mappedBy = "updatedBy")
    private List<MasterLocation> updatedLocationList;
}
