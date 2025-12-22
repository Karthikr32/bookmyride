package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse<T> {

    private ResponseStatus status;
    private String message;
    private T data;


    public ServiceResponse(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }


    public ServiceResponse(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }
}
