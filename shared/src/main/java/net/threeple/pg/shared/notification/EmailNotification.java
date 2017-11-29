package net.threeple.pg.shared.notification;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.util.MailSSLSocketFactory;

import net.threeple.pg.shared.config.ApplicationConfig;

public class EmailNotification {
	Logger logger = LoggerFactory.getLogger(EmailNotification.class);
	private String smtpHost;
	private String smtpUsername;
	private String smtpPassword;
	private String adminEmail;
	
	public EmailNotification() {
		this.smtpHost = ApplicationConfig.getSmtpHost();
		this.smtpUsername = ApplicationConfig.getSmtpUsername();
		this.smtpPassword = ApplicationConfig.getSmtpPassword();
		this.adminEmail = ApplicationConfig.getAdminEmail();
	}
	
	public void send(String subject, String text) {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", this.smtpHost);
		props.setProperty("mail.smtp.auth", "true");
		
		try {
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.socketFactory", sf);
			
			Session session = Session.getInstance(props, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUsername, smtpPassword);
				}
				
			});
			
			 MimeMessage msg = new MimeMessage(session);
			 msg.setFrom(new InternetAddress("PhotoGallery <" + this.smtpUsername + ">"));
			 
			 InternetAddress[] address = {new InternetAddress(this.adminEmail)};
			    msg.setRecipients(Message.RecipientType.TO, address);
			    msg.setSubject(subject);
			    msg.setSentDate(new Date());
			    // If the desired charset is known, you can use
			    // setText(text, charset)
			    msg.setText(text);
			    
			    Transport.send(msg);
		} catch (MessagingException mex) {
		    System.out.println("\n--Exception handling in msgsendsample.java");

		    mex.printStackTrace();
		    System.out.println();
		    Exception ex = mex;
		    do {
			if (ex instanceof SendFailedException) {
			    SendFailedException sfex = (SendFailedException)ex;
			    Address[] invalid = sfex.getInvalidAddresses();
			    if (invalid != null) {
				System.out.println("    ** Invalid Addresses");
				for (int i = 0; i < invalid.length; i++) 
				    System.out.println("         " + invalid[i]);
			    }
			    Address[] validUnsent = sfex.getValidUnsentAddresses();
			    if (validUnsent != null) {
				System.out.println("    ** ValidUnsent Addresses");
				for (int i = 0; i < validUnsent.length; i++) 
				    System.out.println("         "+validUnsent[i]);
			    }
			    Address[] validSent = sfex.getValidSentAddresses();
			    if (validSent != null) {
				System.out.println("    ** ValidSent Addresses");
				for (int i = 0; i < validSent.length; i++) 
				    System.out.println("         "+validSent[i]);
			    }
			}
			System.out.println();
			if (ex instanceof MessagingException)
			    ex = ((MessagingException)ex).getNextException();
			else
			    ex = null;
		    } while (ex != null);
		} catch (GeneralSecurityException e) {
			logger.error("MailSSLSocket异常，异常信息：{}", e.getMessage());
		}
	}
}
