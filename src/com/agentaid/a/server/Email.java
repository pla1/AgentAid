package com.agentaid.a.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.agentaid.a.client.ApplicationException;

public class Email {

	private String bccAddress;
	private String replyToAddress;
	private String body;
	private String ccAddress;
	private final String className = this.getClass().getName();
	private String fromAddress;
	private boolean isHtml;
	private String subject;
	private String title;
	private String toAddress;
	private boolean useDefaultHtmlWrapper = true;
	private String userProfile;
	private ArrayList<String> attachments = new ArrayList<String>();
	private static String webmasterEmailAddress;
	private static String fromEmailAddress;
	private final static Logger logger = Logger.getAnonymousLogger();
	private final static Properties properties = new Properties();

	private static String getDefaultTitle() {
		StringBuilder title = new StringBuilder();
		String ipAddress = "";
		String hostName = "";
		try {
			InetAddress local = InetAddress.getLocalHost();
			ipAddress = local.getHostAddress();
			hostName = local.getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		title.append("Message from: ");
		title.append(ipAddress);
		title.append(" ");
		title.append(hostName);
		title.append(" Date: ");
		title.append(new java.util.Date());
		return title.toString();
	}

	public static void main(String[] args) throws Exception {
		if (true) {
			Email.sendStackTrace(new Throwable("Test exception " + new Date()));
			System.exit(0);
		}

	}

	public static void sendMessageToAdmin(String message) {
		final Email email = new Email();
		email.setToAddress(webmasterEmailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject(message);
		email.setIsHtml(true);
		email.setTitle(getDefaultTitle());
		email.setBody(message);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Email stack trace to Administrator
	 * 
	 * @param exception
	 */
	public static void sendStackTrace(Throwable exception) {
		final Email email = new Email();
		email.setToAddress(webmasterEmailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject("Java exception stack trace - " + exception.getMessage() + " IP: " + Utils.getIpAddress());
		email.setBody(exception, true, null);
		email.setIsHtml(true);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Email stack trace to an email address
	 * 
	 * @param exception
	 * @param emailAddress
	 */
	public static void sendStackTrace(Throwable exception, String emailAddress) {
		final Email email = new Email();
		email.setToAddress(emailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject("Java exception stack trace -  " + exception.getMessage() + " IP: " + Utils.getIpAddress());
		email.setBody(exception, true, null);
		email.setIsHtml(true);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Email stack trace to an email address and specify the subject line.
	 * 
	 * @param exception
	 * @param emailAddress
	 * @param subject
	 */
	public static void sendStackTrace(Throwable exception, String emailAddress, String subject) {
		final Email email = new Email();
		email.setToAddress(emailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject(subject + " IP: " + Utils.getIpAddress());
		email.setBody(exception, true, null);
		email.setIsHtml(true);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Email stack trace to an address. Include a subject and additional
	 * information.
	 * 
	 * @param exception
	 * @param emailAddress
	 * @param subject
	 * @param additionalInformation
	 */
	public static void sendStackTrace(Throwable exception, String emailAddress, String subject, String additionalInformation) {
		final Email email = new Email();
		email.setToAddress(emailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject(subject + " IP: " + Utils.getIpAddress());
		email.setBody(exception, true, additionalInformation);
		email.setIsHtml(true);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The <code>sendStackTraceAlternate</code> method was created to avoid having
	 * to have <code>HttpServletRequest</code> available. Eclipse is saying that
	 * <code>HttpServletRequest</code> is needed when using the
	 * <code>sendStackTrace</code> method.
	 * 
	 * @param exception
	 */
	public static void sendStackTraceAlternate(Throwable exception) {
		final Email email = new Email();
		email.setToAddress(webmasterEmailAddress);
		email.setFromAddress(fromEmailAddress);
		email.setSubject("Java exception stack trace -  " + exception.getMessage() + " IP: " + Utils.getIpAddress());
		email.setTitle(getDefaultTitle());
		email.setBody(exception, true, null);
		email.setIsHtml(true);
		try {
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Email() {
		try {
			properties.load(new FileInputStream("/etc/AgentAid.properties"));
			webmasterEmailAddress = properties.getProperty("emailwebmaster");
			fromEmailAddress = properties.getProperty("emailuser");
			logger.info("From email: " + fromEmailAddress + " webmaster email: " + webmasterEmailAddress);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning(e.getLocalizedMessage());
		}
	}

	public void addAttachment(String fileName) {
		attachments.add(fileName);
	}

	private void addAttachments(Multipart multipart) throws Exception {
		if (attachments.isEmpty()) {
			return;
		}
		for (String fileName : attachments) {
			DataSource source = new FileDataSource(fileName);
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);
		}
	}

	public ArrayList<String> getAttachments() {
		return attachments;
	}

	public String getBccAddress() {
		return bccAddress;
	}

	public String getBody() {
		return body;
	}

	public String getCcAddress() {
		return ccAddress;
	}

	public String getClassName() {
		return className;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	private String getHtmlWrapper(String subject, String body) {
		StringBuilder emailBuffer = new StringBuilder();
		emailBuffer.append("<HTML>\n<HEAD>\n<TITLE>");
		if (title == null || title.length() == 0) {
			emailBuffer.append(subject);
		} else {
			emailBuffer.append(title);
		}
		emailBuffer.append("</TITLE>\n");
		emailBuffer.append("</HEAD>\n<BODY onload='focus()'>\n");
		emailBuffer.append("<H1>");
		if (title == null || title.length() == 0) {
			emailBuffer.append(subject);
		} else {
			emailBuffer.append(title);
		}
		emailBuffer.append("</H1>\n<P>");
		emailBuffer.append(body);
		emailBuffer.append("<!-- This email sent by AS400 java program Email class. -->\n");
		emailBuffer.append("</BODY>\n</HTML>");
		return emailBuffer.toString();
	}

	public String getReplyToAddress() {
		return replyToAddress;
	}

	public String getSubject() {
		return subject;
	}

	public String getTitle() {
		return title;
	}

	public String getToAddress() {
		return toAddress;
	}

	public String getUserProfile() {
		return userProfile;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public boolean isIsHtml() {
		return isHtml;
	}

	public boolean isUseDefaultHtmlWrapper() {
		return useDefaultHtmlWrapper;
	}

	public boolean isUseHtmlWrapper() {
		return useDefaultHtmlWrapper;
	}

	public void send() throws ApplicationException {
		if (Utils.isBlank(body)) {
			throw new ApplicationException("Missing the body to this email. Please provide a body.");
		}
		if (Utils.isBlank(subject)) {
			throw new ApplicationException("Missing the subject to this email. Please provide a subject.");
		}

		if (Utils.isBlank(fromAddress)) {
			throw new ApplicationException("Missing a from email address. Please provide a from address.");
		}
		if (Utils.isBlank(toAddress)) {
			throw new ApplicationException("Missing a to email address. Please provide a to address.");
		}

		Session session = getEmailSession();

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			InternetAddress[] replyTo = { new InternetAddress(fromAddress) };
			message.setReplyTo(replyTo);
			StringTokenizer st = new StringTokenizer(toAddress);
			while (st.hasMoreTokens()) {
				String emailAddress = st.nextToken();
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
			}
			if (!Utils.isBlank(replyToAddress)) {
				st = new StringTokenizer(replyToAddress);
				Address[] addresses = new Address[st.countTokens()];
				int i = 0;
				while (st.hasMoreTokens()) {
					addresses[i] = new InternetAddress(st.nextToken());
				}
				message.setReplyTo(addresses);
			}
			if (ccAddress != null) {
				st = new StringTokenizer(ccAddress);
				while (st.hasMoreTokens()) {
					String emailAddress = st.nextToken();

					message.addRecipient(Message.RecipientType.CC, new InternetAddress(emailAddress));
				}
			}
			if (bccAddress != null) {
				st = new StringTokenizer(bccAddress);
				while (st.hasMoreTokens()) {
					String emailAddress = st.nextToken();

					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailAddress));
				}
			}
			message.setSubject(subject);
			message.setSentDate(new java.util.Date());
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			if (isHtml) {
				if (useDefaultHtmlWrapper) {
					messageBodyPart.setContent(getHtmlWrapper(subject, body), "text/html");
				} else {
					messageBodyPart.setContent(body, "text/html");
				}
			} else {
				messageBodyPart.setContent(body, "text/plain");
			}
			multipart.addBodyPart(messageBodyPart);
			addAttachments(multipart);
			message.setContent(multipart);
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			int i = 0;
			for (StackTraceElement e : stackTraceElements) {
				message.setHeader("X-pla1.net-java-caller-class-name-" + i++, e.getClassName());
			}
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(webmasterEmailAddress));
			Transport.send(message);
		} catch (Exception e) {
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}

	public void setBody(String newBody) {
		body = newBody;
	}

	/**
	 * This is a convenience method for setting the body to stack trace from an
	 * exception. Use it for debugging other classes.
	 * 
	 * @param exception
	 * @param wrapWithHtmlPreTag
	 * @param additionalInformation
	 */
	private void setBody(Throwable exception, boolean wrapWithHtmlPreTag, String additionalInformation) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		if (wrapWithHtmlPreTag) {
			pw.println("<pre>");
		}
		exception.printStackTrace(pw);
		if (wrapWithHtmlPreTag) {
			pw.println("</pre>");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(sw.getBuffer().toString());
		sb.append("<h3>Additional Information</h3>");
		if (!Utils.isBlank(additionalInformation)) {
			if (wrapWithHtmlPreTag) {
				sb.append("<pre>");
			}
			sb.append(additionalInformation);
			if (wrapWithHtmlPreTag) {
				sb.append("</pre>");
			}
		}
		body = sb.toString();
		try {
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.close();
	}

	public void setCcAddress(String newCcAddress) {
		ccAddress = newCcAddress;
	}

	public void setFromAddress(String newFromAddress) {
		fromAddress = newFromAddress;
	}

	public void setIsHtml(boolean newIsHtml) {
		isHtml = newIsHtml;
	}

	public void setReplyToAddress(String newReplyToAddress) {
		toAddress = newReplyToAddress;
	}

	public void setSubject(String newSubject) {
		subject = newSubject;
	}

	public void setTitle(String string) {
		title = string;
	}

	public void setToAddress(String newToAddress) {
		toAddress = newToAddress;
	}

	public void setUseHtmlWrapper(boolean useHtmlWrapper) {
		this.useDefaultHtmlWrapper = useHtmlWrapper;
	}

	public void setUserProfile(String newUserProfile) {
		userProfile = newUserProfile;
	}

	private static Session getEmailSession() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", properties.getProperty("emailserver"));
		props.put("mail.smtp.port", properties.getProperty("emailport"));
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(properties.getProperty("emailuser"), properties.getProperty("emailpassword"));
			}
		});
		return session;
	}
}