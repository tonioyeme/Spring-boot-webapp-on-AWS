package com.example.webapp;

import java.util.HashMap;
import java.util.Map;

public class ObjectMetadata extends com.amazonaws.services.s3.model.ObjectMetadata {

    private long contentLength;
    private String contentType;
    private Map<String, String> userMetadata;

    public ObjectMetadata() {
        this.userMetadata = new HashMap<>();
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public void addUserMetadata(String key, String value) {
        userMetadata.put(key, value);
    }
}
