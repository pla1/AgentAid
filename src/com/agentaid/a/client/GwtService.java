package com.agentaid.a.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface GwtService extends RemoteService {

	Agent getAgentByEmailAddress() throws ApplicationException;

	int updateAgent(Agent agent) throws ApplicationException;

	CachedGeoResult getCachedGeoResult(String address);

	ArrayList<Agent> getAllAgents() throws ApplicationException;

	ArrayList<Comment> getAllComments() throws ApplicationException;

	void vet(int id, boolean vetted) throws ApplicationException;

	void setModerator(int id, boolean vetted) throws ApplicationException;

	Boolean isModerator();
}
