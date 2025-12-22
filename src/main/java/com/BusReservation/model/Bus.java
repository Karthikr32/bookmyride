package com.BusReservation.model;
import com.BusReservation.constants.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String busNumber;

    @Column(nullable = false)
    private String busName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType busType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcType acType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State stateOfRegistration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermitStatus interStatePermitStatus;

    @Column(nullable = false)
    private Long capacity;

    @Column(nullable = false)
    private Long availableSeats;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "from_location_id", nullable = false)
    private MasterLocation fromLocation;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "to_location_id", nullable = false)
    private MasterLocation toLocation;

    @Column(nullable = false)
    private Duration duration;

    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal fare;

    @Column(nullable = false)
    private LocalTime departureAt;

    @Column(nullable = false)
    private LocalTime arrivalAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private Management createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Management updatedBy;

    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private List<Booking> bookings;
}
