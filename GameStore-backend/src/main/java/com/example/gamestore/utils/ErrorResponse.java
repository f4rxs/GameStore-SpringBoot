package com.example.gamestore.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private LocalDateTime timeStamp;
    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.timeStamp = LocalDateTime.now();
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public int getStatus() {
        return status;
    }
}
