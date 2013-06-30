package com.agentaid.a.client;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

public class DecimalKeyPressHandler implements KeyPressHandler {

	public void onKeyPress(KeyPressEvent event) {
		TextBox sender = (TextBox) event.getSource();
		if (sender.isReadOnly() || !sender.isEnabled()) {
			return;
		}
		Character charCode = event.getCharCode();
		int unicodeCharCode = event.getUnicodeCharCode();
		if (!(Character.isDigit(charCode) || charCode == '.' || unicodeCharCode == 0)) {
			sender.cancelKey();
		}
	}

}
