package com.BusReservation.utils;

import com.BusReservation.constants.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse<T> {        // perfect way to built dynamic data binding class

    private ResponseStatus status;
    private String message;
    private T data;


    // for handling error with no data case
    public ServiceResponse(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServiceResponse(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }
}
