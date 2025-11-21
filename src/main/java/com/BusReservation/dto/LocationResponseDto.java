package com.BusReservation.dto;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class LocationResponseDto {    // 12

    private City city;
    private State state;
    private Country country;


    @Data
    public static final class City {   // 8
        private Long id;
        private String city;
        private String createdByName;
        private String createdByRole;
        private LocalDateTime createdAt;
        private String updatedByName;
        private String updatedByRole;
        private LocalDateTime updatedAt;
    }


    @Data
    public static final class State {  // 8
        private Long id;
        private String state;
        private String createdByName;
        private String createdByRole;
        private LocalDateTime createdAt;
        private String updatedByName;
        private String updatedByRole;
        private LocalDateTime updatedAt;
    }


    @Data
    public static final class Country {   // 8
        private Long id;
        private String country;
        private String createdByName;
        private String createdByRole;
        private LocalDateTime createdAt;
        private String updatedByName;
        private String updatedByRole;
        private LocalDateTime updatedAt;
    }

}
