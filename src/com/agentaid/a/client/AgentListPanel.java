package com.agentaid.a.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AgentListPanel extends VerticalPanel {
	private final FlexTable table = new FlexTable();
	private final String[] headers = { "Agent", "Level", "Name", "City", "State", "Zipcode", "Vetted", "Moderator", "ID" };

	public AgentListPanel() {
		add(table);
		addAgentsToTable();
	}

	private void checkModerator() {
		Main.setWorking(true);
		Main.getService().isModerator(new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				if (!result) {
					Main.setWorking(false);
					return;
				}
				int rows = table.getRowCount();
				for (int row = 1; row < rows; row++) {
					final int agentId = Integer.parseInt(table.getText(row, headers.length - 1));
					final CheckBox checkboxVetted = (CheckBox) table.getWidget(row, 6);
					checkboxVetted.setEnabled(true);
					checkboxVetted.addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							vet(agentId, checkboxVetted.getValue());
						}
					});
					final CheckBox checkboxModerator = (CheckBox) table.getWidget(row, 7);
					checkboxModerator.setEnabled(true);
					checkboxModerator.addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							setModerator(agentId, checkboxModerator.getValue());
						}
					});
				}
				Main.setWorking(false);
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}

	private void vet(int id, boolean vetted) {
		Main.setWorking(true);
		Main.getService().vet(id, vetted, new AsyncCallback<Void>() {

			public void onSuccess(Void result) {
				Main.setWorking(false);
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}

	private void setModerator(int id, boolean moderator) {
		Main.setWorking(true);
		Main.getService().setModerator(id, moderator, new AsyncCallback<Void>() {

			public void onSuccess(Void result) {
				Main.setWorking(false);
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}

	private void addAgentsToTable() {
		Main.setWorking(true);
		Utils.setTableHeaderRow(table, headers);
		final CellFormatter cellFormatter = table.getCellFormatter();
		Main.getService().getAllAgents(new AsyncCallback<ArrayList<Agent>>() {

			public void onSuccess(ArrayList<Agent> agents) {
				int row = 1;
				for (Agent agent : agents) {
					int col = 0;
					table.setText(row, col++, agent.getUserName());
					cellFormatter.addStyleName(row, col, "numericCell");
					table.setText(row, col++, Integer.toString(agent.getLevel()));
					table.setText(row, col++, agent.getName());
					table.setText(row, col++, agent.getCity());
					table.setText(row, col++, agent.getState());
					table.setText(row, col++, agent.getZipCode());
					CheckBox checkboxVetted = new CheckBox();
					checkboxVetted.setValue(agent.isVetted());
					checkboxVetted.setEnabled(false);
					table.setWidget(row, col++, checkboxVetted);
					CheckBox checkboxModerator = new CheckBox();
					checkboxModerator.setValue(agent.isModerator());
					checkboxModerator.setEnabled(false);
					table.setWidget(row, col++, checkboxModerator);
					table.setText(row, col++, Integer.toString(agent.getId()));
					row++;
				}
				checkModerator();
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}
}
