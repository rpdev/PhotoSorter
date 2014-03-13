package photosorter;

import java.io.File;

import photosorter.gui.GuiController;
import photosorter.utility.LogFile;

public class ModelController {
	private final SortPhoto sortPhoto;
	private ScanPhoto scanPhoto;
	private CopyPhoto copyPhoto;
	
	ModelController() {
		new GuiController(this);
		sortPhoto = new SortPhoto();
	}
	
	public void start(File startFile, boolean quickMode){
		scanPhoto = new ScanPhoto(startFile, quickMode);
		scanPhoto.registerImageObserver(sortPhoto);
		scanPhoto.start();
	}
	
	public void initCopy(File to, File removedTo){
		scanPhoto = null;
		if(removedTo == null)
			copyPhoto = new CopyPhoto(sortPhoto.getFiles(), to);
		else
			copyPhoto = new CopyPhoto(sortPhoto.getFiles(), to, sortPhoto.getRemovedFiles(), removedTo);
	}
	
	public void startCopy(){
		copyPhoto.start();
	}
	
	public void stop(){
		if(scanPhoto != null)
			scanPhoto.interrupt();
		System.exit(0);
	}
	
	public void removeImage(DataPackage data){
		synchronized(sortPhoto){
			sortPhoto.removeDataPackage(data);
		}
	}
	
	public void addImage(DataPackage data){
		synchronized (sortPhoto) {
			sortPhoto.addDataPackage(data);
		}
	}
	
	public void registerImageObserver(ImageObserver ob){
		sortPhoto.registerImageObserver(ob);
	}
	
	public static void main(String[] args){
		try{
			new ModelController();
		} catch (Exception e) {
			LogFile.writeExceptionToLog(e);
			System.exit(-1);
		}
	}
	
	public void stopCopy(){
		if(!copyPhoto.isDone())
			copyPhoto.stop();
		System.exit(0);
	}
	
	public void registerCopyFileObserver(CopyFileObserver ob){
		copyPhoto.registerCopyFileObserver(ob);
	}

	public void registerFileCountObserver(FileCountObserver ob) {
		if(scanPhoto != null)
			scanPhoto.registerFileCountObserver(ob);
		else if(copyPhoto != null)
			copyPhoto.registerCountObs(ob);
	}
}
