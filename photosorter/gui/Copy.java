package photosorter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import photosorter.CopyFileObserver;
import photosorter.DataPackage;
import photosorter.FileCountObserver;

@SuppressWarnings("serial")
class Copy extends JFrame implements FileCountObserver, CopyFileObserver{
	private final JProgressBar progress = new JProgressBar();
	private final JPanel panel = new JPanel(new GridBagLayout());
	private final GridBagConstraints c = new GridBagConstraints();
	private ImagePanel image = null;
	private final JLabel from = new JLabel("Från: ", JLabel.LEFT), to = new JLabel("Till: ", JLabel.LEFT);
	
	Copy(GuiController control) {
		createFrame(control);
	}

	@Override
	public void copyingFile(final String from, final String to, final DataPackage data) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Copy.this.from.setText("Från: "+from);
				Copy.this.to.setText("Till: "+to);
				if(image != null)
					panel.remove(image);
				image = new ImagePanel(data.getImage());
				image.setSize(new Dimension(data.getImage().getWidth(), data.getImage().getHeight()));
				panel.add(image, c);
				pack();
			}
		});
	}

	@Override
	public void fileCountUpdate(CountType type, int count) {
		if(type == CountType.SINGEL){
			progress.setString(String.format("Kopiera Bilder (%02.1f%%)", (float) count * 100 / (float) progress.getMaximum()));
			progress.setValue(count);
		} else
			progress.setMaximum(count);
	}

	private void createFrame(GuiController control){
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(control.getCloseOperation());
		progress.setStringPainted(true);
		progress.setString("Kopering");
		this.add(progress, BorderLayout.NORTH);
		
		c.gridx = c.gridy = 0;
		panel.add(from, c);
		c.gridy++;
		panel.add(to, c);
		c.gridy+=2;
		JButton exit = new JButton("Avsluta");
		exit.addActionListener(control.getCloseOperationAction());
		panel.add(exit, c);
		c.gridy--;
		this.add(panel, BorderLayout.CENTER);
		this.setVisible(true);
		pack();
	}
}
