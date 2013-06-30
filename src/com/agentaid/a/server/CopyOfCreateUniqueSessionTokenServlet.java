package com.agentaid.a.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CopyOfCreateUniqueSessionTokenServlet extends HttpServlet {

	private static final long serialVersionUID = -6399913492947158097L;
	private static final String APPLICATION_NAME = "Resistance SC";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String state = (String) session.getAttribute("state");
		if (state == null) {
			state = new BigInteger(130, new SecureRandom()).toString(32);
			request.getSession().setAttribute("state", state);
		}
		File test = new File("test.txt");
		Logger.getAnonymousLogger().warning("Path to test file: " + test.getAbsolutePath());
		GoogleApiClientDAO googleApiClientDAO = new GoogleApiClientDAO();
		GoogleApiClient googleApiClient = googleApiClientDAO.get();
		String script = new Scanner(new File("WEB-INF/resources/helper.js"), "UTF-8").useDelimiter("\\A").next()
				.replaceAll("[{]{2}\\s*CLIENT_ID\\s*[}]{2}", googleApiClient.getId()).replaceAll("[{]{2}\\s*STATE\\s*[}]{2}", state)
				.replaceAll("[{]{2}\\s*APPLICATION_NAME\\s*[}]{2}", APPLICATION_NAME);
		response.setContentType("text/javascript");
		PrintWriter pw = response.getWriter();
		pw.write(script);
		pw.write("\n\n\n\n");
		pw.flush();
		pw.close();
	}
}
