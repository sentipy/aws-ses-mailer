package com.sentilabs.mailer.aws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

public class AwsSesMailer {

    private Session session;
    private Transport transport;

    /**
     * Create session and transport used while sending messages
     * @param ses	AmazonSimpleEmailService instance
     *              with information about access and secret keys
     *              for AWS SES
     * @exception MessagingException if some failure occurred while trying to connect to AWS SES
     */
    private void init(AmazonSimpleEmailService ses) throws MessagingException {
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        ses.setRegion(usEast1);
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "aws");

        this.session = Session.getInstance(props);
        this.transport = new AWSJavaMailTransport(session, null);
        this.transport.connect();
    }

    /**
     * Create session and transport used while sending messages
     * @param mailerCredentials	instance of com.sentilabs.mailer.aws.MailerCredentials
     *                          which just implements AWSCredentials
     * @exception MessagingException if some failure occurred while trying to connect to AWS SES
     */
    public AwsSesMailer(MailerCredentials mailerCredentials) throws MessagingException {
        AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(mailerCredentials);
        init(ses);
    }

    /**
     * Create session and transport used while sending messages
     * @param is	input stream from where to read information required for AmazonSimpleEmailService
     * @exception MessagingException if some failure occurred while trying to connect to AWS SES
     */
    public AwsSesMailer(InputStream is) throws MessagingException, IOException {
        AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(new PropertiesCredentials(is));
        init(ses);
    }

    /**
     * Create session and transport used while sending messages
     * @param from	sender email address
     * @param to	recipient of email message
     * @param subject	subject of the email message
     * @param msgText	message text
     * @exception MessagingException if some failure occurred while trying to connect to AWS SES
     */
    public void sendMail(String from, String to, String subject, String msgText) throws MessagingException {
        // Create a new Message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(msgText);
        msg.saveChanges();

        this.transport.sendMessage(msg, null);
    }

    /**
     * refer to documentation of close method of Transport class
     * @throws MessagingException
     */
    public void close() throws MessagingException {
        this.transport.close();
    }

    @Override
    protected void finalize(){
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        try {
            this.transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
