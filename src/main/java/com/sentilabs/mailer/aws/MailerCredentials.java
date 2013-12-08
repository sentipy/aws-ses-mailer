package com.sentilabs.mailer.aws;

import com.amazonaws.auth.AWSCredentials;

public class MailerCredentials implements AWSCredentials {

    private String accessKeyId;
    private String secretKey;

    public MailerCredentials(String accessKeyId, String secretKey){
        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
    }

    @Override
    public String getAWSAccessKeyId() {
        return this.accessKeyId;
    }

    @Override
    public String getAWSSecretKey() {
        return this.secretKey;
    }
}
