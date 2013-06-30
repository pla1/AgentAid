package com.agentaid.a.server;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.agentaid.a.client.Agent;
import com.agentaid.a.client.ApplicationException;
import com.agentaid.a.client.CachedGeoResult;
import com.agentaid.a.client.Comment;
import com.agentaid.a.client.GwtService;
import com.google.api.services.plus.model.Person;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GwtServiceImpl extends RemoteServiceServlet implements GwtService {
	private final static String SESSION_EXPIRED_MESSAGE = "Your session expired. Press F5.";
	private final static Logger logger = Logger.getAnonymousLogger();

	public Agent getAgentByEmailAddress() throws ApplicationException {
		HttpSession session = getThreadLocalRequest().getSession();
		if (session.getAttribute(Utils.Constants.person.name()) == null) {
			throw new ApplicationException(SESSION_EXPIRED_MESSAGE);
		}
		AgentDAO agentDAO = new AgentDAO();
		String emailAddress = (String) session.getAttribute(Utils.Constants.emailAddress.name());
		Person person = (Person) session.getAttribute(Utils.Constants.person.name());
		Person.Image image = person.getImage();
		Agent agent = agentDAO.getByEmailAddress(emailAddress);
		agent.setName(person.getDisplayName());
		agent.setImageUrl(image.getUrl());
		agent.setProfileId(person.getId());
		logger.info(agent.toString());
		return agent;
	}

	public int updateAgent(Agent agent) throws ApplicationException {
		HttpSession session = getThreadLocalRequest().getSession();
		if (session.getAttribute(Utils.Constants.person.name()) == null) {
			throw new ApplicationException(SESSION_EXPIRED_MESSAGE);
		}
		String emailAddress = (String) session.getAttribute(Utils.Constants.emailAddress.name());
		AgentDAO agentDAO = new AgentDAO();
		CommentDAO commentDAO = new CommentDAO();
		if (agent.getId() == 0) {
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "New agent created."));
			Email.sendMessageToAdmin("New agent created at http://agentaid.net. Please vet " + emailAddress);
			return agentDAO.insert(agent);
		} else {
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "Agent updated."));
			return agentDAO.update(agent);
		}
	}

	private String getEmailAddressFromSession() {
		HttpSession session = getThreadLocalRequest().getSession();
		return (String) session.getAttribute(Utils.Constants.emailAddress.name());
	}

	public ArrayList<Agent> getAllAgents() throws ApplicationException {
		HttpSession session = getThreadLocalRequest().getSession();
		if (session.getAttribute(Utils.Constants.person.name()) == null) {
			throw new ApplicationException(SESSION_EXPIRED_MESSAGE);
		}
		AgentDAO agentDAO = new AgentDAO();
		String emailAddress = getEmailAddressFromSession();
		Agent user = agentDAO.getByEmailAddress(emailAddress);
		if (!user.isVetted()) {
			CommentDAO commentDAO = new CommentDAO();
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "User requested all agents but has not been vetted."));
			throw new ApplicationException("You have not been vetted yet. A moderator needs to vet your account. Try again later.");
		}
		return agentDAO.getAll();
	}

	public CachedGeoResult getCachedGeoResult(String address) {
		CachedGeoResultDAO cachedGeoResultDAO = new CachedGeoResultDAO();
		return cachedGeoResultDAO.get(address);
	}

	public ArrayList<Comment> getAllComments() throws ApplicationException {
		AgentDAO agentDAO = new AgentDAO();
		HttpSession session = getThreadLocalRequest().getSession();
		String emailAddress = (String) session.getAttribute(Utils.Constants.emailAddress.name());
		Agent agent = agentDAO.getByEmailAddress(emailAddress);
		CommentDAO commentDAO = new CommentDAO();
		if (agent.isModerator()) {
			return commentDAO.getAllComments();
		} else {
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "Tried to get logs and is not a moderator."));
			throw new ApplicationException(emailAddress + " is not a moderator.");
		}
	}

	public Boolean isModerator() {
		AgentDAO agentDAO = new AgentDAO();
		String emailAddress = getEmailAddressFromSession();
		Agent user = agentDAO.getByEmailAddress(emailAddress);
		logger.info("Check if " + emailAddress + " is a moderator and the answer is: " + user.isModerator());
		return user.isModerator();
	}

	public void setModerator(int id, boolean moderator) throws ApplicationException {
		AgentDAO agentDAO = new AgentDAO();
		String emailAddress = getEmailAddressFromSession();
		logger.info(emailAddress + " setting moderator agent ID: " + id + " with value: " + moderator);
		Agent user = agentDAO.getByEmailAddress(emailAddress);
		if (!user.isModerator()) {
			throw new ApplicationException("You must be a moderator to set another agent's role at http://agentaid.net.");
		}
		Agent agent = agentDAO.getById(id);
		agent.setModerator(moderator);
		int quantity = agentDAO.update(agent);
		if (quantity == 1) {
			CommentDAO commentDAO = new CommentDAO();
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "Changed moderator boolean to " + moderator
					+ " for agent with email address: " + agent.getEmailAddress()));
		} else {
			Email.sendMessageToAdmin("setModerator routine failed in GwtServiceImpl. User: " + emailAddress + " agent: "
					+ agent.getEmailAddress());
		}
	}

	public void vet(int id, boolean vetted) throws ApplicationException {
		AgentDAO agentDAO = new AgentDAO();
		String emailAddress = getEmailAddressFromSession();
		logger.info(emailAddress + " vetting agent ID: " + id + " with value: " + vetted);
		Agent user = agentDAO.getByEmailAddress(emailAddress);
		if (!user.isModerator()) {
			throw new ApplicationException("You must be a moderator to vet an agent at http://agentaid.net.");
		}
		Agent agent = agentDAO.getById(id);
		agent.setVetted(vetted);
		int quantity = agentDAO.update(agent);
		if (quantity == 1) {
			CommentDAO commentDAO = new CommentDAO();
			commentDAO.insert(new Comment(Comment.TYPE_LOG, emailAddress, "Changed vetted boolean to " + vetted
					+ " for agent with email address: " + agent.getEmailAddress()));
		} else {
			Email
					.sendMessageToAdmin("vet routine failed in GwtServiceImpl. User: " + emailAddress + " agent: " + agent.getEmailAddress());
		}
	}
}
