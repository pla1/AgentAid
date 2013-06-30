package com.agentaid.a.server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import javax.mail.Session;

public class Utils {
	public static enum Constants {
		person, emailAddress, state, token, tokenResponse, me
	}

	public boolean isDevelopmentEnvironment() {
		return (getIpAddress().startsWith("192.168.1"));
	}

	/**
	 * This method returns the local machines IP address.
	 * 
	 * NOTE: Creating a socket and getting the address from it works on Linux
	 * where the InetAddress.getLocalHost().getHostAddress() does not. It return
	 * the loopback address 127.0.0.1 in Linux.
	 * 
	 * @return ipAddress String
	 */
	public static String getIpAddress() {
		String ipAddress = "";
		try {
			Socket socket = new Socket("google.com", 80);
			InetAddress inetAddress = socket.getLocalAddress();
			if (inetAddress != null) {
				ipAddress = inetAddress.getHostAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipAddress;
	}

	public static boolean isYes(String s) {
		return s.equals("Y");
	}

	public static String isYes(boolean b) {
		if (b) {
			return "Y";
		} else {
			return "N";
		}
	}

	public static boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getClassNames() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StringBuilder classNames = new StringBuilder();
		for (StackTraceElement e : stackTraceElements) {
			classNames.append(e.getClassName()).append(", ");
		}
		if (classNames.toString().endsWith(", ")) {
			classNames.delete(classNames.length() - 2, classNames.length());
		}
		return classNames.toString();
	}
	
}
