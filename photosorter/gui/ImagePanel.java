package photosorter.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
class ImagePanel extends JPanel{
	private final BufferedImage image;
	
	ImagePanel(BufferedImage image){
		this.image = image;
		Dimension dim = new Dimension(image.getWidth(), image.getHeight());
		this.setPreferredSize(dim);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(image, 0, 0, null);
	}
}