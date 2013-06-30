package com.agentaid.a.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GwtServiceAsync {
	void getAgentByEmailAddress(AsyncCallback<Agent> callback);

	void updateAgent(Agent agent, AsyncCallback<Integer> callback);

	void getCachedGeoResult(String address, AsyncCallback<CachedGeoResult> callback);

	void getAllAgents(AsyncCallback<ArrayList<Agent>> callback);

	void getAllComments(AsyncCallback<ArrayList<Comment>> callback);

	void vet(int id, boolean vetted, AsyncCallback<Void> callback);

	void setModerator(int id, boolean moderator, AsyncCallback<Void> callback);

	void isModerator(AsyncCallback<Boolean> callback);
}
