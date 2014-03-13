package photosorter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import photosorter.DataPackage;

@SuppressWarnings("serial")
class MonthView extends JPanel{
	private final HashMap<DataPackage, ImageView> imageViews = new HashMap<DataPackage, ImageView>();
	private final GridBagConstraints c = new GridBagConstraints();
	private final JPanel panel = new JPanel(new GridBagLayout());
	private final GuiController control;
	private final boolean quickMode;
	private final int titleHeight, columns = 900 / 210;
	
	MonthView(GuiController control, boolean quickMode, String tag){
		this.control = control;
		this.quickMode = quickMode;
		c.gridx = c.gridy = 0; c.insets = new Insets(10, 0, 0, 10);
		
		this.setLayout(new BorderLayout());
		
		JLabel title = new JLabel(tag, JLabel.CENTER);
		title.setOpaque(true);
		title.setBackground(Color.ORANGE);
		titleHeight = title.getPreferredSize().height;
		this.add(title, BorderLayout.NORTH);
		
		this.add(new JScrollPane(panel), BorderLayout.CENTER);
	}
	
	void addImage(DataPackage data){
		ImageView imageView = new ImageView(control, data, quickMode);
		synchronized (imageViews) {
			imageViews.put(data, imageView);
			addImageView(imageView);
			revalidate();
		}
	}
	
	void addImage(DataPackage data, ImageView imageView){
		synchronized (imageViews) {
			imageViews.put(data, imageView);
			addImageView(imageView);
			revalidate();
		}
	}
	
	ImageView removeImage(DataPackage data){
		ImageView imageView = imageViews.remove(data);
		reAddAll();
		return imageView;
	}
	
	int getTotalHeight(){
		return panel.getPreferredSize().height + titleHeight + (c.gridy + 1) * 5;
	}
	
	private void reAddAll(){
		panel.removeAll();
		c.gridx = c.gridy = 0;
		synchronized (imageViews) {
			for(ImageView iv : imageViews.values())
				addImageView(iv);
		}
		revalidate();
	}

	private void addImageView(ImageView imageView){
		if(columns <= c.gridx){
			c.gridy++;
			c.gridx=0;
		}
		panel.add(imageView, c);
		c.gridx++;
	}
}
