package com.agentaid.a.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.agentaid.a.client.Comment;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;

public class SigninCallbackServlet extends HttpServlet {

	private static final long serialVersionUID = -6399913492947158097L;
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static final Gson GSON = new Gson();
	private static final Logger logger = Logger.getAnonymousLogger();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String state = (String) session.getAttribute(Utils.Constants.state.name());
		if (state == null) {
			state = new BigInteger(130, new SecureRandom()).toString(32);
			request.getSession().setAttribute(Utils.Constants.state.name(), state);
		}
		logger.info("start state is: " + state);
		InputStream inputStream = request.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String code = bufferedReader.readLine();
		logger.info("Authorization code is: " + code);
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		String tokenData = (String) session.getAttribute(Utils.Constants.token.name());
		if (tokenData != null) {
			response.setStatus(400);
			pw.write(GSON.toJson("Current user is already connected."));
			pw.flush();
			pw.close();
			return;
		}
		String stateFromRequest = request.getParameter(Utils.Constants.state.name());
		logger.info("State from request: " + stateFromRequest + " state from session: " + state);
		if (!state.equals(stateFromRequest)) {
			response.setStatus(401);
			pw.write(GSON.toJson("Invalid state parameter."));
			pw.flush();
			pw.close();
			return;
		}
		GoogleApiClientDAO googleApiClientDAO = new GoogleApiClientDAO();
		GoogleApiClient googleApiClient = googleApiClientDAO.get();
		GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY, googleApiClient.getId(),
				googleApiClient.getSecret(), code, "postmessage").execute();
		GoogleIdToken idToken = tokenResponse.parseIdToken();
		String gplus_id = idToken.getPayload().getUserId();
		String emailAddress = idToken.getPayload().getEmail();
		session.setAttribute(Utils.Constants.emailAddress.name(), emailAddress);
		logger.info("Google ID token: " + idToken.getPayload().toString() + " gplus ID: " + gplus_id + " email address: "
				+ emailAddress);
		session.setAttribute(Utils.Constants.tokenResponse.name(), tokenResponse);
		String message = "Did not get your email address for some reason.";
		if (emailAddress != null && emailAddress.contains("@")) {
			message = emailAddress;
			GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY).setTransport(TRANSPORT)
					.setClientSecrets(googleApiClient.getId(), googleApiClient.getSecret()).build().setFromTokenResponse(tokenResponse);
			Plus service = new Plus.Builder(TRANSPORT, JSON_FACTORY, credential).setApplicationName("Agent Aid").build();
			Person person = service.people().get(Utils.Constants.me.name()).execute();
			logger.info("Adding person to session:" + person.getDisplayName());
			session.setAttribute(Utils.Constants.person.name(), person);
			CommentDAO commentDAO = new CommentDAO();
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "User logged in."));
		}
		pw.write(GSON.toJson(message));
		pw.flush();
		pw.close();
	}
}
