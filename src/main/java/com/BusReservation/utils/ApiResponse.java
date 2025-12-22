package com.BusReservation.utils;
import com.BusReservation.constants.Code;

import java.time.LocalDateTime;
import java.util.*;


public class ApiResponse {

    public static Map<String, Object> statusMsg(int status, Code code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("staus", status);
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }


    public static <T> Map<String, Object> successStatusMsgData(int status, String message, T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("staus", status);
        response.put("message", message);
        response.put("data", data);
        return response;
    }


    public static <T> Map<String, Object> errorStatusMsgErrors(int status, Code code, T errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("code", code);
        response.put("error", errors);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }


    public static Map<String, Object> usernameAndToken(String username, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", token);
        return response;
    }


    public static Map<String, Object> profileStatus(int status, Code code, String message, boolean profileStatus) {
        Map<String, Object> response = new HashMap<>();
        response.put("staus", status);
        response.put("code", code);
        response.put("message", message);
        response.put("profileCompleted", profileStatus);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }


    public static <T> Map<String, Object> partialSuccessStatus(int status, Code code, String message, T errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("staus", status);
        response.put("code", code);
        response.put("message", message);
        response.put("error", errors);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}