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
public class Bus {    // total -> 22 | Exclude 3 (Id/bookings/version) -> 19 | 19 fields to entry

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
    private AcType acType;               // AC or NON_AC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;           // SEATER or SLEEPER or SEATER_SLEEPER

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State stateOfRegistration;                 // TN/kerala/andra/delhi | can Identify using Bus number

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermitStatus interStatePermitStatus;    // yes/no

    @Column(nullable = false)
    private Long capacity;

    @Column(nullable = false)
    private Long availableSeats;     // (capacity - seatsBooked)

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "from_location_id", nullable = false)
    private MasterLocation fromLocation;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "to_location_id", nullable = false)
    private MasterLocation toLocation;

    @Column(nullable = false)
    private Duration duration;      // PT??H??M -> eg: PT05H47M -> 05 hour 47 minutes

    @Column(precision = 6, scale = 2, nullable = false)   // precision(total) - 6 digits (9,999.99) | scale(after point) - 2 (.99)
    private BigDecimal fare;     // modified

    @Column(nullable = false)          // no need to be unique
    private LocalTime departureAt;     // NOT BG, want to get this from ADMIN via dto (data_java : HH:mm:ss | data_DB : HH:mm:ss)

    @Column(nullable = false)
    private LocalTime arrivalAt;       // Bg departureAt + bus_duration

    @Version
    @Column(nullable = false)
    private Long version;             // Do not touch

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private Management createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;     // Bg

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Management updatedBy;

    private LocalDateTime updatedAt;     // Bg

    @JsonIgnore
    @OneToMany(mappedBy = "bus")           // map using field name not column name
    private List<Booking> bookings;
}
