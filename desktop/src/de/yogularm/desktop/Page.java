package de.yogularm.desktop;

import javax.swing.JPanel;

public class Page extends JPanel {
	private static final long serialVersionUID = 8910777354476713537L;
	
	private SwingLauncher launcher;
	
	public Page(SwingLauncher launcher) {
		this.launcher = launcher;
	}
	
	public SwingLauncher getLauncher() {
		return launcher;
	}
	
	public void onShown() {
		
	}
	
	public void onHidden() {
		
	}
}
