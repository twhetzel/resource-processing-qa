package com.neuinfo.resourceworkflow;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	private static final Logger LOG = Logger.getLogger(SendEmail.class.getSimpleName());
	
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String emailMsgTxt1 = "The following resources are stuck in processing:\n";
	private static final String emailMsgTxt2 = "\n\nThe NIF QA Team";	
	private static final String emailSubjectTxt = "DISCO Resources Stuck in Processing";
	private static final String emailFromAddress = "whetzel@ucsd.edu";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String[] sendTo = {"whetzel@ucsd.edu","nif-curators@mail.neuinfo.org"};

	public static void emailCurators(ArrayList<String> resourcesToReview) throws Exception {

		ArrayList<String> resourceListToEmail = resourcesToReview;
		int numberOfResourcesToEmail = resourceListToEmail.size();
		
		// Reformat resource list for cleaner format in email
		String formatedResourceString = resourceListToEmail.toString()
                .replace(",", "\n")  //remove the commas
                .replace("[", " ")   //remove the right bracket
                .replace("]", ""); //remove the left bracket
		LOG.info("STUCK RESOURCES: \n"+formatedResourceString+"\n(Total Stuck Resources: "+numberOfResourcesToEmail+")");
		
		new SendEmail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt1+formatedResourceString+
				"\nTotal resource count: "+numberOfResourcesToEmail+emailMsgTxt2, emailFromAddress);
		LOG.info("Sucessfully Sent mail to All Users");
	}

	
	public void sendSSLMessage(String recipients[], String subject,
			String message, String from) throws MessagingException {
		boolean debug = false;

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("YourEmailAccount", "YourEmailPWD");
			}
		});

		session.setDebug(debug);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		Transport.send(msg);
	}
}

