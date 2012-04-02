package de.yogularm.desktop;

import java.awt.Color;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

public class CustomTextField extends JTextField {
	private static final long serialVersionUID = -3164432663413246874L;

	private static final String HIDDEN_SUFFIX = "\0";
	private static final Color PLACEHOLDER_COLOR = Color.gray;
	
	private String placeholder;
	private Color defaultColor;
	
	public String getPlaceholder() {
		return placeholder;
	}
	
	public void setPlaceholder(String value) {
		if (value == null)
			value = "";
		this.placeholder = value;
		showPlaceholder();
	}
	
	@Override
	protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		switch (e.getID()) {
		case FocusEvent.FOCUS_GAINED:
				hidePlaceholder();
			break;
		case FocusEvent.FOCUS_LOST:
			showPlaceholder();
		}
	}
	
	private void showPlaceholder() {
		if (getText().equals("")) {
			setText(placeholder + HIDDEN_SUFFIX);
			defaultColor = getForeground();
			setForeground(PLACEHOLDER_COLOR);
		}
	}
	
	private void hidePlaceholder() {
		if (getText().equals(placeholder + HIDDEN_SUFFIX)) {
			setText("");
			setForeground(defaultColor);
		}
	}
}
