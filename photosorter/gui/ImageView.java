package photosorter.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import photosorter.DataPackage;

@SuppressWarnings("serial")
class ImageView extends JLayeredPane{
	private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private final MouseAdapter removeAdapter, addAdapter;
	
	ImageView(final GuiController control, final DataPackage data, boolean quickMode){
		createPanel(data, quickMode);
		removeAdapter = createRemoveListener(control, data);
		addAdapter = createAddListener(control, data);
		this.addMouseListener(removeAdapter);
	}
	
	void changeMouseAdapter(boolean remove){
		if(remove){
			this.removeMouseListener(addAdapter);
			this.addMouseListener(removeAdapter);
		} else {
			this.removeMouseListener(removeAdapter);
			this.addMouseListener(addAdapter);
		}
		this.repaint();
	}
	
	private void createPanel(final DataPackage data, boolean quickMode){
		JPanel defaultPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		
		JLabel date = new JLabel(df.format(data.getDate()), JLabel.CENTER);
		defaultPanel.add(date, c);
		if(!quickMode && data.hasImage()){
			c.gridy++;
			defaultPanel.add(new ImagePanel(data.getImage()), c);
		}
		
		JLabel name = new JLabel(data.getFile().getName(), JLabel.CENTER);
		JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER));
		if(data.isLastModified()){
			JPanel lastMod = new JPanel();
			lastMod.setBackground(Color.ORANGE);
			lastMod.setPreferredSize(new Dimension(20, name.getPreferredSize().height));
			lastMod.setToolTipText("Foto datum bestämt genom 'senast ändrad'.");
			info.add(lastMod);
		} 
		info.add(name);
		if(!quickMode && !data.hasImage()){
			JPanel hasImage = new JPanel();
			hasImage.setBackground(Color.RED);
			hasImage.setToolTipText("Kan ej läsa bild");
			hasImage.setPreferredSize(new Dimension(20, name.getPreferredSize().height));
			info.add(hasImage);
		}				
		
		c.gridy++;
		defaultPanel.add(info, c);
		this.setLayout(null);
		this.setPreferredSize(defaultPanel.getPreferredSize());
		defaultPanel.setBounds(0, 0, defaultPanel.getPreferredSize().width, defaultPanel.getPreferredSize().height);
		this.setLayer(defaultPanel, JLayeredPane.DEFAULT_LAYER);
		this.add(defaultPanel, JLayeredPane.DEFAULT_LAYER);
	}
	
	private MouseAdapter createRemoveListener(final GuiController control, final DataPackage data){
		MouseAdapter remove = new MouseAdapter(){
			private JButton removeButton;
			private boolean showingButton = false;
			private final int layer = JLayeredPane.PALETTE_LAYER;
			
			@Override
			public void mouseEntered(MouseEvent me){
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						if(removeButton == null)
							initRemoveButton();
						showingButton = true;
						ImageView.this.setLayer(removeButton, layer);
						ImageView.this.add(removeButton, layer);
						ImageView.this.repaint();
					}
					
					private void initRemoveButton(){
						removeButton = new JButton("Ta bort");
						Dimension iD = ImageView.this.getPreferredSize();
						Dimension rD = removeButton.getPreferredSize();
						int xCenter = iD.width/2 - rD.width/2;
						int yCenter = iD.height/2 - rD.height/2;
						removeButton.setBounds(xCenter, yCenter, rD.width, rD.height);
						removeButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								EventQueue.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										showingButton = false;
										ImageView.this.remove(removeButton);
										ImageView.this.repaint();
										control.removeImage(data);
									}
								});
							}
						});
					}
				});
			}
			
			@Override
			public void mouseExited(MouseEvent me){
				Point p = me.getPoint();
				Dimension d = ImageView.this.getPreferredSize();
				if(showingButton && (p.x + 10 >= d.width || p.y + 10 >= d.height || p.x - 10 <= 0 || p.y - 10 <= 0)){
					showingButton = false;
					ImageView.this.remove(removeButton);
					ImageView.this.repaint();
				}
			}
		};
		return remove;
	}
	
	private MouseAdapter createAddListener(final GuiController control, final DataPackage data){
		MouseAdapter add = new MouseAdapter() {
			private JButton addButton = null;
			private boolean showingButton = false;
			private final int layer = JLayeredPane.PALETTE_LAYER;
			
			@Override
			public void mouseEntered(MouseEvent me){
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						if(addButton == null)
							initAddButton();
						if(!showingButton){
							showingButton = true;
							ImageView.this.setLayer(addButton, layer);
							ImageView.this.add(addButton, layer);
							ImageView.this.repaint();
						}
					}
					
					private void initAddButton(){
						addButton = new JButton("Lägg till");
						Dimension iD = ImageView.this.getPreferredSize();
						Dimension rD = addButton.getPreferredSize();
						int xCenter = iD.width/2 - rD.width/2;
						int yCenter = iD.height/2 - rD.height/2;
						addButton.setBounds(xCenter, yCenter, rD.width, rD.height);
						addButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								EventQueue.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										showingButton = false;
										ImageView.this.remove(addButton);
										ImageView.this.repaint();
										control.addImage(data);
									}
								});
							}
						});
					}
				});
			}
			
			@Override
			public void mouseExited(MouseEvent me){
				Point p = me.getPoint();
				Dimension d = ImageView.this.getPreferredSize();
				if(showingButton && (p.x + 10 >= d.width || p.y + 10 >= d.height || p.x - 10 <= 0 || p.y - 10 <= 0)){
					showingButton = false;
					ImageView.this.remove(addButton);
					ImageView.this.repaint();
				}
			}
		};
		return add;
	}
}