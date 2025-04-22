package com.tieinternational;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private String status;
    private String role;
    private String message;

    // Add default constructor for Jackson deserialization
    public LoginResponse() {
    }

    public LoginResponse(String status, String role, String message) {
        this.status = status;
        this.role = role;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}