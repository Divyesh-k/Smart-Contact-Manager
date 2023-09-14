package SmarterContactManager.SmarterContactManager.services;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;



@Service
public class EmailService {
	
	public boolean sendEmail(String subject , String message , String to) {
		
		String from = "demo75317531@gmail.com";
		
		//rest of the code........
		
		//variable for gmail host
		String host = "smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		
//		setting the properties info
//		set host
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
//		step:1 to get the session object...
		Session session = Session.getInstance(properties , new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("demo75317531@gmail.com" , "test75317531");
			}
		});
		
		session.setDebug(true);
//		step 2 : compose the message[text , multi , media]
		MimeMessage compose = new MimeMessage(session);
		try {
			//from email
			compose.setFrom(from);
			
			//adding recepient
			compose.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
//			adding subject to the message
			compose.setSubject(subject);
			
//			adding text to message
			compose.setText(message);
			
//			step-2:send the message using Transport class
			Transport.send(compose);
			
		} catch (MessagingException e) {
			
			e.printStackTrace();
			return false;
			
		}
		
		return true;
	}
	
}
