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
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @JsonIgnore
    @ManyToOne
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
    private LocalDateTime bookedAt;

    @Column(nullable = false)
    private LocalDate travelAt;

    private LocalDateTime canceledAt;

    @Column(nullable = false)
    private LocalDateTime departureAt;

    @Column(nullable = false)
    private LocalDateTime arrivalAt;

    private LocalDateTime userEditedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime bookingExpiresAt;

    @Column(unique = true)
    private String busTicket;

    @Column(unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Integer discountPct;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalCost;

    @Version
    @Column(nullable = false)
    private Long version;
}