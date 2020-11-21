package com.wavemaker.connector.twilio.authy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    @Value("${twilio.account.SID}")
    private String twilioSID;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.authy.api.key}")
    private String authyAPIKey;

    public String getTwilioSID() {
        return twilioSID;
    }

    public void setTwilioSID(String twilioSID) {
        this.twilioSID = twilioSID;
    }

    public String getTwilioAuthToken() {
        return twilioAuthToken;
    }

    public void setTwilioAuthToken(String twilioAuthToken) {
        this.twilioAuthToken = twilioAuthToken;
    }

    public String getAuthyAPIKey() {
        return authyAPIKey;
    }

    public void setAuthyAPIKey(String authyAPIKey) {
        this.authyAPIKey = authyAPIKey;
    }
}
