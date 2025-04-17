package com.tie_international.model;

import java.sql.Timestamp;

public class Log {
    private int id;
    private String action;
    private Timestamp timestamp;

    public Log(int id, String action, Timestamp timestamp) {
        this.id = id;
        this.action = action;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}