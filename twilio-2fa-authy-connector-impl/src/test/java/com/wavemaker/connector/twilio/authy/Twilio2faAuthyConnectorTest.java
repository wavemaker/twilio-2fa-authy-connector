package com.wavemaker.connector.twilio.authy;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import com.wavemaker.connector.twilio.authy.Twilio2faAuthyConnector;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Twilio2faAuthyConnectorTestConfiguration.class)
public class Twilio2faAuthyConnectorTest {

    @Autowired
    private Twilio2faAuthyConnector twilio2faConnector;

    @Test
    public void registerUser(){
        int authyId = twilio2faConnector.registerUser("sunil.pulugula@wavemaker.com", "9642723789", "+91");
        Assert.assertFalse("authyId should not be null", authyId==0);
    }

    @Test
    public void sendSMSOTP(){
        int authyId = twilio2faConnector.registerUser("sunil.pulugula@wavemaker.com", "9642723789", "+91");
        System.out.println("Authy Id " + authyId);
        twilio2faConnector.sendSMSToken(authyId);
    }

    @Test
    public void verifySMSOTP(){
        int authyId = twilio2faConnector.registerUser("sunil.pulugula@wavemaker.com", "9642723789", "+91");
        System.out.println("Authy Id " + authyId);
        twilio2faConnector.verifyOTP(authyId,"1572813");
    }

    @Test
    public void verifyVoiceOTP(){
        int authyId = twilio2faConnector.registerUser("sunil.pulugula@wavemaker.com", "9642723789", "+91");
        System.out.println("Authy Id " + authyId);
        twilio2faConnector.verifyOTP(authyId,"1572813");
    }

    @Test
    public void startSMSVerification(){
        twilio2faConnector.startVerification("9642723789","+91", "sms");
    }

    @Test
    public void checkSMSVerification(){
        boolean result = twilio2faConnector.checkVerification("9642723789", "+91", "8887");
        Assert.assertTrue("verification is not successful",result);
    }

    @Test
    public void startVoiceVerification(){
        twilio2faConnector.startVerification("9642723789","+91", "call");
    }
}