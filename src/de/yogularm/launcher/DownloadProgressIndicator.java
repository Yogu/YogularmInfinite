package de.yogularm.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class DownloadProgressIndicator {
	JFrame frame = null;
	JProgressBar progressBar = null;
	boolean uiCreated = false;
	
	public static void main(String[] args) {
		DownloadProgressIndicator app = new DownloadProgressIndicator();
		try {
			app.progress(new URL("http://www.yogularm.de/infinite/code/yogularm.jar"), "1.0", 0, 0, 1);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DownloadProgressIndicator() {

	}

	private void create() {
		JPanel top = createComponents();
		frame = new JFrame("Downloading Yogularm Infinite..."); // top level custom progress indicator UI
		frame.getContentPane().add(top, BorderLayout.CENTER);
		frame.setBounds(300, 300, 400, 300);
		frame.pack();
		frame.setVisible(true);
		updateProgressUI(0);
	}

	private JPanel createComponents() {
		JPanel top = new JPanel();
		top.setBackground(Color.WHITE);
		top.setLayout(new BorderLayout(20, 20));

		String lblText = "One moment please, Yogu is surfing towards you...";
		JLabel lbl = new JLabel(lblText);
		top.add(lbl, BorderLayout.NORTH);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		top.add(progressBar, BorderLayout.SOUTH);

		return top;
	}

	public void progress(URL url, String version, long readSoFar, long total,
			int overallPercent) {
		updateProgressUI(overallPercent);

	}

	public void upgradingArchive(java.net.URL url, java.lang.String version,
			int patchPercent, int overallPercent) {
		updateProgressUI(overallPercent);
	}

	public void validating(java.net.URL url, java.lang.String version,
			long entry, long total, int overallPercent) {
		updateProgressUI(overallPercent);
	}

	private void updateProgressUI(int overallPercent) {
		if (overallPercent > 0 && overallPercent < 99) {
			if (!uiCreated) {
				uiCreated = true;
				// create custom progress indicator's UI only if
				// there is more work to do, meaning overallPercent > 0 and <
				// 100
				// this prevents flashing when RIA is loaded from cache
				create();
			}
			progressBar.setValue(overallPercent);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame.setVisible(true);
				}
			});
		} else {
			// hide frame when overallPercent is above 99
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame.setVisible(false);
					frame.dispose();
				}
			});
		}
	}
}