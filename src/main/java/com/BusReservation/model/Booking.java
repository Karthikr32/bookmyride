package com.BusReservation.model;

import com.BusReservation.constants.BookingStatus;
import com.BusReservation.constants.PaymentMethod;
import com.BusReservation.constants.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {       // 23 fields W/O ID ==> 22

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne       // One Customer can have Many Bookings
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @JsonIgnore
    @ManyToOne     // One Bus can have Many Bookings
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "from_location_id", nullable = false)
    private MasterLocation fromLocation;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "to_location_id", nullable = false)
    private MasterLocation toLocation;

    @Column(nullable = false)
    private Long seatsBooked;

    @Column(nullable = false)
    private LocalDateTime bookedAt;    // current date

    @Column(nullable = false)
    private LocalDate travelAt;     // only date from user. (MODIFIED)

    private LocalDateTime canceledAt;   // current date when user clicks "CANCEL" button

    @Column(nullable = false)
    private LocalDateTime departureAt;     // get from admin via bus dto

    @Column(nullable = false)
    private LocalDateTime arrivalAt;        // auto-calculate

    private LocalDateTime userEditedAt;    // updates when user clicks edit/update mode when after checking BookingPreview DTO

    @Enumerated(EnumType.STRING)      // saves as enum value! via backend
    @Column(nullable = false)
    private BookingStatus bookingStatus;   // Initially default, then update vice versa

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;   // Initially default, then update vice versa

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;   // Initially default, then update vice versa

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")   // storing as String and in pattern as hour:min:sec at the Indian time zone
    private LocalDateTime bookingExpiresAt;        // current time + 5-10 min

    @Column(unique = true)
    private String busTicket;         // initially none, then generate value

    @Column(unique = true)
    private String transactionId;     // initially pending, then generate value

    // new fields for discount feature
    @Column(nullable = false)
    private Integer discountPct;      // discount percentage %

    @Column(nullable = false, precision = 10, scale = 2)     // total 10 digits | 2 decimal accepted after point (eg: 99999999.99)
    private BigDecimal totalCost;       // calculated by backend  (fare * seatsBooked)

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalCost;

    @Version
    @Column(nullable = false)
    private Long version;             // Do not touch
}


// In relationship fields @Column should not be used with @JsonColumn