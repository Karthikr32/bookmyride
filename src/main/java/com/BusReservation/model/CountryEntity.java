package com.BusReservation.model;

import com.BusReservation.constants.Country;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "countries")
public class CountryEntity {   // 7 -> 5

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Country name;

    @OneToMany(mappedBy = "country")
    private List<StateEntity> states;   // DND

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Management createdById;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Management updatedById;

    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
