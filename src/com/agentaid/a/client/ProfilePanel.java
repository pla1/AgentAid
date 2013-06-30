package com.agentaid.a.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProfilePanel extends VerticalPanel {
	private final FlexTable table = new FlexTable();
	private final TextBox userNameField = new TextBox();
	private final TextBox cityField = new TextBox();
	private final TextBox zipcodeField = new TextBox();
	private final TextBox longitudeField = new TextBox();
	private final TextBox latitudeField = new TextBox();
	private final StateListBox stateListBox = new StateListBox();
	private final Button saveButton = new Button("Save");
	private final LevelListBox levelListBox = new LevelListBox();
	private final Label geoResultLabel = new Label();
	private Agent agent;

	public ProfilePanel() {
		Main.setWorking(true);
		add(table);
		add(saveButton);
		saveButton.setEnabled(false);
		loadAgent();
		saveButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String errors = validate();
				if (isBlank(errors)) {
					update();
				} else {
					Window.alert(errors);
				}
			}
		});
		latitudeField.addKeyPressHandler(new DecimalKeyPressHandler());
		longitudeField.addKeyPressHandler(new DecimalKeyPressHandler());
		zipcodeField.addKeyPressHandler(new IntegerKeyPressHandler());
		zipcodeField.addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {
				if (zipcodeField.getText().trim().length() > 4) {
					if (isAddressFieldsEmpty()) {
						geoResultLabel.setText("");
						geo();
					}
				}
			}
		});
	}

	private void addTableRow(int row, String label, String value) {
		table.setText(row, 0, label);
		table.setText(row, 1, value);
	}

	private void addTableRow(int row, String label, Widget value) {
		table.setText(row, 0, label);
		table.setWidget(row, 1, value);
	}

	private void ableAddressFields(boolean b) {
		cityField.setEnabled(b);
		zipcodeField.setEnabled(b);
		stateListBox.setEnabled(b);
		saveButton.setEnabled(b);
		latitudeField.setEnabled(b);
		longitudeField.setEnabled(b);
	}

	private void geo() {
		Main.setWorking(true);
		ableAddressFields(false);
		Main.getService().getCachedGeoResult(zipcodeField.getText().trim(), new AsyncCallback<CachedGeoResult>() {

			public void onFailure(Throwable caught) {
				ableAddressFields(true);
				Main.setWorking(false);
			}

			public void onSuccess(CachedGeoResult result) {
				if (isAddressFieldsEmpty()) {
					latitudeField.setText(Double.toString(result.getLatitude()));
					longitudeField.setText(Double.toString(result.getLongitude()));
					cityField.setText(result.getCity());
					zipcodeField.setText(result.getZipcode());
					setStateListBox(result.getState());
					geoResultLabel.setText("Coordinates retrieved for address: " + result.getFormattedAddress());
				}
				ableAddressFields(true);
				Main.setWorking(false);
			}
		});

	}

	private boolean isAddressFieldsEmpty() {
		if (!isBlank(cityField.getText())) {
			return false;
		}
		if (stateListBox.getSelectedIndex() > 0) {
			return false;
		}
		if ("0.0".equals(latitudeField.getText()) || "0".equals(latitudeField.getText())
				|| latitudeField.getText().trim().length() == 0) {
			if ("0".equals(longitudeField.getText()) || "0.0".equals(longitudeField.getText())
					|| longitudeField.getText().trim().length() == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	private void loadAgent() {
		Main.getService().getAgentByEmailAddress(new AsyncCallback<Agent>() {

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				saveButton.setEnabled(true);
				Main.setWorking(false);
			}

			public void onSuccess(Agent result) {
				agent = result;
				int i = 0;
				table.setWidget(i++, 0, new Image(agent.getImageUrl()));
				addTableRow(i++, "Email:", agent.getEmailAddress());
				addTableRow(i++, "Name:", agent.getName());
				userNameField.setText(agent.getUserName());
				cityField.setText(agent.getCity());
				setStateListBox(agent.getState());
				zipcodeField.setText(agent.getZipCode());
				latitudeField.setText(Double.toString(agent.getLatitude()));
				longitudeField.setText(Double.toString(agent.getLongitude()));
				userNameField.setText(agent.getUserName());
				if (agent.getLevel() < levelListBox.getItemCount()) {
					levelListBox.setSelectedIndex(agent.getLevel());
				}
				addTableRow(i++, "Agent name:", userNameField);
				addTableRow(i++, "Level", levelListBox);
				addTableRow(i++, "Vetted", Boolean.toString(agent.isVetted()));
				addTableRow(i++, "Moderator", Boolean.toString(agent.isModerator()));
				addTableRow(i++, "Zip code:", zipcodeField);
				addTableRow(i++, "State:", stateListBox);
				addTableRow(i++, "City:", cityField);
				addTableRow(i++, "Latitude:", latitudeField);
				addTableRow(i++, "Longitude:", longitudeField);
				addTableRow(i++, "", geoResultLabel);
				saveButton.setEnabled(true);
				Main.setWorking(false);
			}
		});
	}

	private void setStateListBox(String stateString) {
		if (isBlank(stateString)) {
			return;
		}
		for (int i = 0; i < stateListBox.getItemCount(); i++) {
			if (stateString.equals(stateListBox.getValue(i))) {
				stateListBox.setSelectedIndex(i);
			}
		}
	}

	private void update() {
		Main.setWorking(true);
		agent.setUserName(userNameField.getText());
		agent.setCity(cityField.getText());
		agent.setLatitude(Double.parseDouble(latitudeField.getText()));
		agent.setLongitude(Double.parseDouble(longitudeField.getText()));
		agent.setState(stateListBox.getValue(stateListBox.getSelectedIndex()));
		agent.setZipCode(zipcodeField.getText());
		agent.setLevel(levelListBox.getSelectedIndex());
		Main.getService().updateAgent(agent, new AsyncCallback<Integer>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}

			public void onSuccess(Integer result) {
				if (result.intValue() != 1) {
					Window.alert("Update failed");
				} else {
					RootPanel.get("content").clear();
					History.newItem("");
				}
				Main.setWorking(false);
			}
		});
	}

	private String validate() {
		StringBuilder sb = new StringBuilder();
		if (isBlank(userNameField.getText())) {
			sb.append("\nUser name can not be blank.");
		}
		if (cityField.getText().trim().length() > 50) {
			sb.append("\nCity it too long. Limit to 50 characters or less.");
		}
		if (!isBlank(zipcodeField.getText())) {
			if (zipcodeField.getText().length() != 5 && zipcodeField.getText().length() != 9) {
				sb.append("\nZipcode should be blank, 5, or 9 numbers.");
			}
		}
		if (levelListBox.getSelectedIndex() == 0) {
			sb.append("\nPlease select your level.");
		}
		return sb.toString();
	}
}
