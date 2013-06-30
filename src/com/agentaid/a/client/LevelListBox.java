package com.agentaid.a.client;

import com.google.gwt.user.client.ui.ListBox;

public class LevelListBox extends ListBox {
	public LevelListBox() {
		addItem("..");
		for (int i = 1; i < 9; i++) {
			addItem(Integer.toString(i));
		}
	}

}
