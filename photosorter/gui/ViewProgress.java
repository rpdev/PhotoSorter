package photosorter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import photosorter.DataPackage;
import photosorter.FileCountObserver;
import photosorter.ImageObserver;

@SuppressWarnings("serial")
class ViewProgress extends JFrame implements FileCountObserver, ImageObserver{
	private final TreeMap<String, MonthView> struct = new TreeMap<String, MonthView>(new Comparator<String>() {
		@Override
		public int compare(String s1, String s2){
			Integer i1 = new Integer(s1.substring(0, s1.indexOf('-')));
			Integer i2 = new Integer(s2.substring(0, s2.indexOf('-')));
			if(i1.intValue() != i2.intValue())
				return i1.compareTo(i2);
			i1 = new Integer(s1.substring(s1.indexOf('-')+1, s1.lastIndexOf(' ')));
			i2 = new Integer(s2.substring(s2.indexOf('-')+1, s2.lastIndexOf(' ')));
			return i1.compareTo(i2);
		}
	});
	private final JPanel mainPanel = new JPanel(new GridBagLayout());
	private final GridBagConstraints c = new GridBagConstraints();
	private final Dimension frameDim = new Dimension(1000,600), mainDim = new Dimension(900,600), removeDim = new Dimension(0, 600);
	private final boolean quickMode;
	private final GuiController control;
	private final JProgressBar progress = new JProgressBar();
	private HashMap<DataPackage, ImageView> removedImages;
	private JPanel removedPanel, removedMain;
	private final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
	private JButton button;
	
	ViewProgress(GuiController control, boolean quickMode){
		this.control = control;
		this.quickMode = quickMode;
		createFrame(control);
	}
	
	@Override
	public void imageUpdate(final String tag, final DataPackage data) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(!struct.containsKey(tag)){
					MonthView view = new MonthView(control, quickMode, tag);
					view.setPreferredSize(mainDim);
					struct.put(tag, view);
					mainPanel.add(view, c);
					c.gridy++;
				}
				
				if(removedImages != null && removedImages.containsKey(data)){
					ImageView imageView = removedImages.remove(data);
					imageView.changeMouseAdapter(true);
					removedPanel.remove(imageView);
					removedPanel.revalidate();
					struct.get(tag).addImage(data, imageView);
					if(removedImages.size() == 0)
						removeRemovedPanel();
				} else
					struct.get(tag).addImage(data);
				validate();
				pack();
			}
			
			private void removeRemovedPanel(){
				remove(removedMain);
				removedMain = null;
				removedImages = null;
				removedPanel = null;
				frameDim.width = mainDim.width + 20;
				removeDim.width = 0;
			}
		});
	}
	
	@Override
	public void imageRemoved(final String tag, final DataPackage data) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(removedPanel == null)
					initRemovedPanel();
				ImageView imageView = struct.get(tag).removeImage(data);
				imageView.changeMouseAdapter(false);
				if(imageView.getPreferredSize().width > removeDim.width){
					removeDim.width = imageView.getPreferredSize().width + 20;
					frameDim.width += removeDim.width;
				}
				removedImages.put(data, imageView);
				removedPanel.add(imageView);
				validate();
				pack();
			}
			
			private void initRemovedPanel(){
				removedPanel = new JPanel(new GridLayout(0, 1, 0, 10));
				removedImages = new HashMap<DataPackage, ImageView>();
				removedMain = new JPanel(new BorderLayout());
				JLabel title = new JLabel("Borttagna", JLabel.CENTER);
				title.setOpaque(true);
				title.setBackground(Color.RED);
				removedMain.add(title, BorderLayout.NORTH);
				removedMain.add(new JScrollPane(removedPanel), BorderLayout.CENTER);
				removedMain.setPreferredSize(removeDim);
				ViewProgress.this.add(removedMain, BorderLayout.EAST);
			}
		});
	}

	@Override
	public void fileCountUpdate(CountType type, int count) {
		if(type == CountType.SINGEL){
			progress.setString(String.format("Importera Bilder %.1f%%  (%d / %d)", (float) count * 100 / (float) progress.getMaximum(), count, progress.getMaximum()));
			progress.setValue(count);
			if(progress.getMaximum() == count){
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						mainPanel.removeAll();
						c.gridy = 0;
						for(MonthView j : struct.values()){
							if(j.getTotalHeight() < mainDim.height)
								j.setPreferredSize(new Dimension(mainDim.width, j.getTotalHeight()));
							j.revalidate();
							mainPanel.add(j, c);
							c.gridy++;
						}
						validate();
						pack();
						button.removeActionListener(button.getActionListeners()[0]);
						button.setText("Kopiera");
						button.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								EventQueue.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										control.startCopy();
									}
								});
							}
						});
					}
				});
			}
		}
		else
			progress.setMaximum(count);
	}

	private void createFrame(final GuiController control) {
		this.addWindowListener(control.getCloseOperation());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setPreferredSize(frameDim);
		
		progress.setStringPainted(true);
		progress.setString("Scan Progress");
		this.add(progress, BorderLayout.NORTH);
		
		this.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
		c.gridx = c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5d;
		
		ActionListener stop = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e){
				control.abort();
				button.setEnabled(false);
			}
		};
		button = new JButton("Avbryt inläsning");
		button.addActionListener(stop);
		buttons.add(button);
		JButton exit = new JButton("Avsluta");
		exit.addActionListener(stop);
		buttons.add(exit);
		this.add(buttons, BorderLayout.SOUTH);
		
		this.setVisible(true);
		this.pack();
		this.validate();
	}
}
