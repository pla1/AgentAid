package com.agentaid.a.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

public class Utils {

	public static boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void setTableHeaderRow(FlexTable table, String[] headers) {
		CellFormatter cellFormatter = table.getFlexCellFormatter();
		for (int i = 0; i < headers.length; i++) {
			table.setText(0, i, headers[i]);
			cellFormatter.addStyleName(0, i, "tableHeader");
		}
	}
}
