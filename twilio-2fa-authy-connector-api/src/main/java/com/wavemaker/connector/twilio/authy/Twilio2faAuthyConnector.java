package com.wavemaker.connector.twilio.authy;

import com.wavemaker.runtime.connector.annotation.WMConnector;


@WMConnector(name = "twilio-2fa-authy-connector",
        description = "Twilio 2fa connector to used enable two factor authentication for WaveMaker application")
public interface Twilio2faAuthyConnector {

    public void sendSMSToken(Integer authyId);

    public void sendVoiceToken(Integer authyId);

    public String sendOneTouchApproval(Integer authyId,String username, String emailId, String message);

    public int registerUser(String email, String phoneNumber, String countryCode);

    public boolean verifyOTP(Integer authyId, String token);

    public String retrieveOneTouchStatus(String uuid);

    public void startVerification(String phoneNumber, String countryCode, String via);

    public boolean checkVerification(String phoneNumber, String countryCode, String code);

}