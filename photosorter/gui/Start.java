package photosorter.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import photosorter.utility.FileUtility;

@SuppressWarnings("serial")
class Start extends JFrame{
	private File sourceFile, targetFile, optionFile;
	
	Start(GuiController control){
		createFrame(control);
	}
	
	private void createFrame(final GuiController control){
		this.setLayout(new GridBagLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final int fieldLength = 15, iconWidth = 15, iconHeight = 10;
		final Insets defaultInsets = new Insets(0, 0, 0, 5), spaceInsets = new Insets(10, 0, 0, 0);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0; c.anchor = GridBagConstraints.WEST;
		
		
		this.add(new JLabel("Källa:"), c);
		
		c.gridy++;
		final JTextField sourceField = new JTextField(fieldLength);
		sourceField.addFocusListener(new FocusListener() {
			private String old;
			
			@Override
			public void focusLost(FocusEvent e) {
				File file = new File(sourceField.getText());
				if(file.exists())
					sourceFile = file;
				else
					sourceField.setText(old);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				old = sourceField.getText();
			}
		});		
		this.add(sourceField, c);
		
		c.gridx++;
		JButton sourceButton = new JButton(createFolderIcon(iconWidth, iconHeight));
		sourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = FileUtility.openFileChooser(getRootPane(), JFileChooser.DIRECTORIES_ONLY);
				if(file != null){
					sourceField.setText(file.getAbsolutePath());
					sourceFile = file;
				}
			}
		});
		this.add(sourceButton, c);
		
		
		c.gridx = 0; c.gridy++; c.insets = spaceInsets;
		this.add(new JLabel("Mål:"), c);
		
		c.gridy++; c.insets = defaultInsets;
		final JTextField targetField = new JTextField(fieldLength);
		targetField.addFocusListener(new FocusListener() {
			private String old;
			
			@Override
			public void focusLost(FocusEvent e) {
				if(targetField.getText().length() > 0){
					File file = new File(targetField.getText());
					if(!file.exists()){
						int choice = JOptionPane.showConfirmDialog(getRootPane(), "Katalogen ["+ file.getAbsolutePath() +"] finns inte, skapa?", "Ny katalog?", JOptionPane.OK_CANCEL_OPTION);
						if(choice == JOptionPane.OK_OPTION)
							file.mkdirs();
						else
							targetField.setText(old);
					} else{
						if(file.isFile())
							JOptionPane.showMessageDialog(getRootPane(), "Det är en fil, målet måste vara en katalog.", "Det är en fil", JOptionPane.ERROR_MESSAGE);
						else
							targetFile = file;
					}
				} else
					targetField.setText(old);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				old = targetField.getText();			
			}
		});
		this.add(targetField, c);
		
		c.gridx++;
		JButton targetButton = new JButton(createFolderIcon(iconWidth, iconHeight));
		targetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = FileUtility.saveFileChooser(getRootPane(), JFileChooser.DIRECTORIES_ONLY);
				if(file != null){
					targetField.setText(file.getAbsolutePath());
					targetFile = file;
				}
			}
		});
		this.add(targetButton, c);
		
		
		c.gridx = 0; c.gridy++; c.insets = spaceInsets;
		this.add(new JLabel("Felaktiga bilder (frivillig)"), c);
		
		c.gridy++; c.insets = defaultInsets;
		final JTextField optionField = new JTextField(fieldLength);
		optionField.addFocusListener(new FocusListener() {
			private String old;
			
			@Override
			public void focusLost(FocusEvent e) {
				if(optionField.getText().length() > 0){
					File file = new File(optionField.getText());
					if(!file.exists()){
						int choice = JOptionPane.showConfirmDialog(getRootPane(), "Katalogen ["+ file.getAbsolutePath() +"] finns inte, skapa?", "Ny katalog?", JOptionPane.OK_CANCEL_OPTION);
						if(choice == JOptionPane.OK_OPTION)
							file.mkdirs();
						else
							optionField.setText(old);
					} else{
						if(file.isFile())
							JOptionPane.showMessageDialog(getRootPane(), "Det är en fil, målet måste vara en katalog.", "Det är en fil", JOptionPane.ERROR_MESSAGE);
						else
							optionFile = file;
					}
				} else
					optionField.setText(old);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				old = optionField.getText();			
			}
		});
		this.add(optionField, c);
		
		c.gridx++;
		JButton optionButton = new JButton(createFolderIcon(iconWidth, iconHeight));
		optionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = FileUtility.saveFileChooser(getRootPane(), JFileChooser.DIRECTORIES_ONLY);
				if(file != null){
					optionField.setText(file.getAbsolutePath());
					optionFile = file;
				}
			}
		});
		this.add(optionButton, c);
		
		c.gridx = 0; c.gridy++; c.gridwidth = 2;
		JPanel optionPanel = new JPanel(new FlowLayout());
		JButton startButton = new JButton("Start");
		JButton exitButton = new JButton("Avsluta");
		exitButton.addActionListener(control.getCloseOperationAction());
		final JCheckBox quickModeBox = new JCheckBox("Quick Mode");
		optionPanel.add(startButton);
		optionPanel.add(exitButton);
		optionPanel.add(quickModeBox);
		this.add(optionPanel, c);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						if(sourceFile == null)
							JOptionPane.showMessageDialog(getRootPane(), "Finns ingen plats att hämta filerna från.");
						else if(targetFile == null)
							JOptionPane.showMessageDialog(getRootPane(), "Finns ingen plats att koperia filerna till.");
						else
							control.start(new File[]{sourceFile, targetFile, optionFile}, quickModeBox.isSelected());
					}
				});
			}
		});
		this.setVisible(true);
		this.validate();
		this.pack();
	}

	private static ImageIcon createFolderIcon(int width, int height){
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(width/6, 0, (int) ((4f/6f)*width), height);
		g2.setColor(Color.BLUE);
		g2.fillRect(0, height/3, width, (int) ((2f/3f)*height));
		return new ImageIcon(image);
	}


}
