package com.agentaid.a.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint, ValueChangeHandler<String> {

	private static final GwtServiceAsync gwtService = GWT.create(GwtService.class);
	private Panel contentPanel;
	private static Label workingLabel = new Label("Working...");

	public void onModuleLoad() {
		workingLabel.addStyleName("working");
		HorizontalPanel linksPanel = new HorizontalPanel();
		Panel panel = RootPanel.get("header");
		contentPanel = RootPanel.get("content");
		linksPanel.add(new Hyperlink("Profile", "Profile"));
		linksPanel.add(new Hyperlink("List", "List"));
		linksPanel.add(new Hyperlink("Map", "Map"));
		panel.add(linksPanel);
		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		if (Utils.isBlank(event.getValue())) {
			return;
		}
		contentPanel.clear();
		if ("Profile".equals(event.getValue())) {
			contentPanel.add(new ProfilePanel());
		}
		if ("List".equals(event.getValue())) {
			contentPanel.add(new AgentListPanel());
		}
		if ("Map".equals(event.getValue())) {
			contentPanel.add(new MapPanel());
		}
		if (event.getValue().toLowerCase().startsWith("log")) {
			contentPanel.add(new CommentPanel());
		}
	}

	public static GwtServiceAsync getService() {
		return gwtService;
	}

	public static void setWorking(boolean b) {
		if (b) {
			RootPanel.get().add(workingLabel, 0, 0);
		} else {
			workingLabel.removeFromParent();
		}
	}
}
