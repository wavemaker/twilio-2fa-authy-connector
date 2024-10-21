package com.wavemaker.connector.twilio.authy.service;

import com.authy.AuthyApiClient;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class TwilioService {

    @Autowired
    PropertyService propertyService;

    private AuthyApiClient client = null;

    @PostConstruct
    public void init() {
        Twilio.init(propertyService.getTwilioSID(), propertyService.getTwilioAuthToken());
    }

    public AuthyApiClient getAuthyClient() {
        if (client == null) {
            client = new AuthyApiClient(propertyService.getAuthyAPIKey());
        }
        return client;
    }
}
