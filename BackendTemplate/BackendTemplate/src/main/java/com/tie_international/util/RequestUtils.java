package com.tie_international.server;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestUtils {

    public static String extractUsername(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("username");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON: Missing or invalid 'username' field", e);
        }
    }

    public static String extractPassword(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("password");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON: Missing or invalid 'password' field", e);
        }
    }
}