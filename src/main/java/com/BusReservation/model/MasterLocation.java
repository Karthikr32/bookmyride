package com.BusReservation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class MasterLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @ManyToOne
    @JoinColumn(nullable = false, name = "created_by")
    private Management createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Management updatedBy;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fromLocation")
    private List<Bus> busesDepartingFrom;

    @OneToMany(mappedBy = "toLocation")
    private List<Bus> busesDepartingTo;
}
