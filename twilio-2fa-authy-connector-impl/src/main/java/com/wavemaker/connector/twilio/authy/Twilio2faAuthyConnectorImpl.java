package com.wavemaker.connector.twilio.authy;

import com.authy.AuthyException;
import com.authy.OneTouchException;
import com.authy.api.*;
import com.wavemaker.connector.twilio.authy.exception.AuthyRequestException;
import com.wavemaker.connector.twilio.authy.service.PropertyService;
import com.wavemaker.connector.twilio.authy.service.TwilioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class Twilio2faAuthyConnectorImpl implements Twilio2faAuthyConnector{

    private static final Logger logger = LoggerFactory.getLogger(Twilio2faAuthyConnectorImpl.class);

    public static final String PHONE_VERIFICATION_API_PATH = "/protected/json/phones/verification/";
    public static final String DEFAULT_API_URI = "https://api.authy.com";

    @Autowired
    private TwilioService twilioService;

    @Autowired
    PropertyService propertyService;

    public void sendSMSToken(Integer authyId) {
        try {
            logger.info("Sending sms token to authyID " + authyId);
            Hash result = twilioService.getAuthyClient().getUsers().requestSms(authyId);
            if (!result.isSuccess()) {
                throw new AuthyRequestException(result.getError().getMessage());
            }
        } catch (AuthyException e) {
            logger.info("Failed to send sms token to authyID " + authyId);
            throw new AuthyRequestException("Failed to send SMS token ", e);
        }
    }

    public void sendVoiceToken(Integer authyId) {
        try {
            logger.info("Sending voice token to authyID " + authyId);
            Hash result = twilioService.getAuthyClient().getUsers().requestCall(authyId);
            if (!result.isSuccess()) {
                throw new AuthyRequestException(result.getError().getMessage());
            }
        } catch (AuthyException e) {
            logger.info("Failed to send voice token to authyID " + authyId);
            throw new AuthyRequestException("Failed to send voice token  ", e);
        }
    }

    public String sendOneTouchApproval(Integer authyId, String username, String emailId, String message) {
        try {
            if (hasAuthyApp(authyId)) {
                logger.info("Sending one touch token to authyID " + authyId);
                ApprovalRequestParams parameters = new ApprovalRequestParams.Builder(
                        Integer.valueOf(authyId),
                        message)
                        .addDetail("Username", username)
                        .addDetail("Authy ID", String.valueOf(authyId))
                        .addDetail("Email", emailId)
                        .build();
                OneTouchResponse result = twilioService.getAuthyClient().getOneTouch().sendApprovalRequest(parameters);
                if (!result.isSuccess()) {
                    logger.error("Failed to send one touch token to authy id" + authyId, result.getMessage());
                    throw new AuthyRequestException(result.getMessage());
                }
                return result.getApprovalRequest().getUUID();
            }
        } catch (AuthyException e) {
            logger.error("Failed to send one touch token to authy id" + authyId, e);
            throw new AuthyRequestException("Failed to send one touch approval request  ", e);
        }
        return null;
    }

    public int registerUser(String email, String phoneNumber, String countryCode) {
        try {
            logger.info("Registering user to twilio with email id " + email);
            com.authy.api.User authyUser = twilioService.getAuthyClient().getUsers().createUser(email, phoneNumber, countryCode);
            if (authyUser.isOk()) {
                logger.info("Registered user in twilio with authy id " + authyUser.getId());
                return authyUser.getId();
            } else {
                throw new AuthyRequestException(authyUser.getError().getMessage());
            }

        } catch (AuthyException e) {
            logger.error("Failed to register user in twilio with email id " + email);
            throw new AuthyRequestException(e.getMessage());
        }
    }

    public boolean verifyOTP(Integer authyId, String token) {
        try {
            logger.info("Verifying authy id {0} and token {1} with twilio ", authyId, token);
            Token verificationResult = twilioService.getAuthyClient()
                    .getTokens()
                    .verify(authyId, token);

            return verificationResult.isOk();
        } catch (AuthyException e) {
            logger.error("Failed to verify given authy Id {0} and token {1}", authyId, token);
            throw new AuthyRequestException("Failed to verify token", e);
        }
    }

    public String retrieveOneTouchStatus(String uuid) {
        try {
            logger.info("Retrieving one touch status from twilio using uuid {0}", uuid);
            return twilioService.getAuthyClient()
                    .getOneTouch()
                    .getApprovalRequestStatus(uuid)
                    .getApprovalRequest()
                    .getStatus();
        } catch (OneTouchException e) {
            logger.error("Failed to retrieve one touch status from twilio using uuid {0}", uuid);
            throw new AuthyRequestException("Failed to retrieve one touch status", e);
        }
    }

    public void startVerification(String phoneNumber, String countryCode, String via) {
        logger.info("Sending token for verification using phoneno {0} via channel {1}", phoneNumber, via);
        Params params = new Params();
        params.setAttribute("phone_number", phoneNumber);
        params.setAttribute("country_code", countryCode);
        params.setAttribute("via", via);

        try {
            final Resource resource = new Resource(DEFAULT_API_URI, propertyService.getAuthyAPIKey());
            Resource.Response response = resource.post(PHONE_VERIFICATION_API_PATH + "start", params);

            Verification verification = new Verification(response.getStatus(), response.getBody());
            if (!verification.isOk()) {
                String body = response.getBody();
                logger.error("Failed to send token for verification ", body);
                throw new AuthyRequestException(body);
            }

        } catch (AuthyException e) {
            logger.error("Failed to send token for verification using phoneno {0} via channel {1}", phoneNumber, via);
            throw new AuthyRequestException("Failed to verify token", e);
        }

    }

    public boolean checkVerification(String phoneNumber, String countryCode, String code) {
        logger.info("Verifying given code {0} with phonenumber {1} ", code, phoneNumber);
        ;
        Params params = new Params();
        params.setAttribute("phone_number", phoneNumber);
        params.setAttribute("country_code", countryCode);
        params.setAttribute("verification_code", code);

        try {
            final Resource resource = new Resource(DEFAULT_API_URI, propertyService.getAuthyAPIKey());
            Resource.Response response = resource.get(PHONE_VERIFICATION_API_PATH + "check", params);

            Verification verification = new Verification(response.getStatus(), response.getBody());
            if (!verification.isOk()) {
                String body = response.getBody();
                logger.error("Failed to verify code with reason ", body);
                throw new AuthyRequestException(body);
            }
            return true;
        } catch (AuthyException e) {
            logger.error("Failed to verify given code {0} with phonenumber {1} ", code, phoneNumber);
            throw new AuthyRequestException("Failed to verify token", e);
        }

    }

    private boolean hasAuthyApp(Integer authyId) throws AuthyException {
        return twilioService.getAuthyClient().getUsers().requestStatus(authyId).isRegistered();
    }
}