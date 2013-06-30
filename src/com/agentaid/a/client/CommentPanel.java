package com.agentaid.a.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel extends VerticalPanel {
	private final static FlexTable table = new FlexTable();
	private final String[] headers = { "Email", "Time", "Text" };

	public CommentPanel() {
		Label refreshLabel = new Label("Refresh");
		refreshLabel.addStyleName("clickable");
		add(refreshLabel);
		add(table);
		load();
		refreshLabel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				load();
			}
		});
	}

	private void load() {
		Main.setWorking(true);
		table.removeAllRows();
		Utils.setTableHeaderRow(table, headers);
		Main.getService().getAllComments(new AsyncCallback<ArrayList<Comment>>() {

			public void onSuccess(ArrayList<Comment> comments) {
				int row = 1;
				for (Comment comment : comments) {
					int col = 0;
					table.setText(row, col++, comment.getEmailAddress());
					table.setText(row, col++, comment.getTimeDisplay());
					table.setText(row, col++, comment.getText());
					row++;
				}
				Main.setWorking(false);
			}

			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
				Main.setWorking(false);
			}
		});
	}
}
