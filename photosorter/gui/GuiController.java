package photosorter.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import photosorter.DataPackage;
import photosorter.ModelController;

public class GuiController {
	private final ModelController modelController;
	private JFrame start, scan, copy;
	private File[] files;
	private final ActionListener actionClose = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(scan != null)
				modelController.stop();
			else if(copy != null)
				modelController.stopCopy();
			else if(start != null)
				System.exit(0);
		}
	};
	private final WindowAdapter windowClose = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent we){
			int choise = JOptionPane.showConfirmDialog(null, "Exit?");
			if(choise == JOptionPane.OK_OPTION){
				if(scan != null)
					modelController.stop();
				else if(copy != null)
					modelController.stopCopy();
				else if(start != null)
					System.exit(0);
			}
		}
	};
	public GuiController(ModelController modelController){
		this.modelController = modelController;
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				start = new Start(GuiController.this);
			}
		});
	}
	
	void startCopy(){
		scan.dispose();
		scan = null;
		(new Thread(){
			@Override
			public void run(){
				modelController.initCopy(files[1], files[2]);
				copy = new Copy(GuiController.this);
				modelController.registerCopyFileObserver((Copy) copy);
				modelController.registerFileCountObserver((Copy) copy);
				modelController.startCopy();
			}
		}).start();
	}
	
	void stopCopy(){
		modelController.stopCopy();
	}
	
	void start(final File[] files, final boolean quickMode){
		this.files = files;
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				start.dispose();
				ViewProgress view = new ViewProgress(GuiController.this, quickMode);
				modelController.registerImageObserver(view);
				modelController.start(files[0], quickMode);
				modelController.registerFileCountObserver(view);
				scan = view;
			}
		});
	}
	
	void abort() {
		(new Thread() {
			@Override
			public void run() {
				modelController.stop();
			}
		}).start();
	}

	WindowAdapter getCloseOperation() {
		return windowClose;
	}
	
	ActionListener getCloseOperationAction(){
		return actionClose;
	}
	
	void addImage(final DataPackage data){
		(new Thread(){
			@Override
			public void run(){
				modelController.addImage(data);
			}
		}).start();
	}

	void removeImage(final DataPackage data) {
		(new Thread(){
			@Override
			public void run(){
				modelController.removeImage(data);
			}
		}).start();
	}
}
