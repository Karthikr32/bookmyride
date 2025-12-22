package com.BusReservation.model;

import com.BusReservation.constants.State;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "states")
public class StateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private State name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Management createdById;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Management updatedById;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "state")
    private List<CityEntity> cities;

    @Version
    private Integer version;
}
