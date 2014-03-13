package photosorter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import photosorter.DataPackage.ImageType;
import photosorter.FileCountObserver.CountType;
import photosorter.utility.FileUtility;
import photosorter.utility.LogFile;
import photosorter.utility.PhotoUtility;
import photosorter.utility.TagNotFound;

class ScanPhoto extends Thread{
	private final static PhotoFilter FILTER = new PhotoFilter();
	private final ArrayList<FileCountObserver> fileCountObservers = new ArrayList<FileCountObserver>();
	private final ArrayList<ImageUpdate> imageObservers = new ArrayList<ImageUpdate>();
	private final boolean quickMode;
	private final File file;
	private int count = 0;
	private boolean stop = false;
	private Thread interuptCallerThread;
	
	interface ImageUpdate {
		void newImage(DataPackage data);
	}
	
	public ScanPhoto(File file, boolean quickMode){
		this.file = file;
		this.quickMode = quickMode;
	}
	
	@Override
	public void interrupt(){
		if(!this.isAlive())
			return;
		stop = true;
		interuptCallerThread = Thread.currentThread();
		synchronized (interuptCallerThread) {
			try {
				interuptCallerThread.wait();
			} catch (InterruptedException e) {
				LogFile.writeExceptionToLog(e);
			}
		}
	}
	
	@Override
	public void run(){
		notifyFileCountObservers(CountType.TOTAL, FileUtility.fileCount(file, FILTER));
		try {
			ArrayList<File> files = new ArrayList<File>();
			files.add(file);
			while(!stop && !files.isEmpty()){
				File file = files.remove(0);
				if(file.isDirectory())
					files.addAll(Arrays.asList(file.listFiles(FILTER)));
				else
					processFile(file);
			}
			if(interuptCallerThread!=null)
				synchronized (interuptCallerThread) {
					interuptCallerThread.notify();
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerFileCountObserver(FileCountObserver ob){
		fileCountObservers.add(ob);
	}
	
	public void registerImageObserver(ImageUpdate ob){
		imageObservers.add(ob);
	}
	
	private void notifyFileCountObservers(CountType type, int count){
		for(FileCountObserver ob : fileCountObservers)
			ob.fileCountUpdate(type, count);
	}
	
	private void notifyImageObservers(Data data){
		for(ImageUpdate ob : imageObservers)
			ob.newImage(data);
	}
	
	private void processFile(File file) throws IOException{
		String fileEx = FileUtility.getFileExtension(file);
		Data data;
		if(fileEx.equals(".jpg") || fileEx.equals(".jpeg")){
			Metadata meta = null;
			Date[] dates = null;
			try{
				meta = ImageMetadataReader.readMetadata(file);
				dates = PhotoUtility.getData(meta);
			}
			catch (ImageProcessingException e){}
			catch (TagNotFound e) {}
			finally {
				if(meta != null && dates != null)
					data = new Data(file, dates[0], ImageType.JPG, false);
				else
					data = new Data(file, new Date(file.lastModified()), ImageType.JPG, true);
			}
		} else 
			data = new Data(file, new Date(file.lastModified()), ImageType.valueOf(fileEx.substring(1).toUpperCase()), true);
		notifyFileCountObservers(CountType.SINGEL, ++count);
		notifyImageObservers(data);
	}
	
	private static final class PhotoFilter implements FileFilter{
		private final String[] allowedTypes = {".png",".jpg",".jpeg",".gif",".bmp"};

		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory())
				return true;
			String name = pathname.getName();
			if(!name.contains("."))
				return false;
			String type = name.substring(name.lastIndexOf('.')).toLowerCase();
			for(String s : allowedTypes)
				if(type.equals(s))
					return true;
			return false;
		}
		
	}
	
	private class Data implements DataPackage{
		private final Date date;
		private final File file;
		private final ImageType type;
		private final BufferedImage image;
		private final boolean lastModified, hasImage;
		private final static float MAX_SIZE = 200;
		
		private Data(File file, Date date, ImageType type, boolean lastModified){
			this.date = date;
			this.file = file;
			this.type = type;
			this.lastModified = lastModified;
			image = !quickMode ? getImage(file) : null;
			hasImage = (image != null);
		}
		
		private BufferedImage getImage(File file){
			ImageInputStream iis = null;
			BufferedImage image = null;
			try {
				iis = ImageIO.createImageInputStream(file);
				Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
				if(!it.hasNext())
					throw new UnsupportedOperationException("No image reader fround.");
				ImageReader reader = it.next();
				reader.setInput(iis);
				int scaleFactor;
				if(reader.getWidth(0) >= reader.getHeight(0))
					scaleFactor = Math.round(((float) reader.getWidth(0))/ MAX_SIZE);
				else
					scaleFactor = Math.round(((float) reader.getHeight(0))/MAX_SIZE);
				ImageReadParam param = reader.getDefaultReadParam();
				param.setSourceSubsampling(scaleFactor, scaleFactor, 0, 0);
				image = reader.read(0, param);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(iis != null)
					try {
						iis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			return image;
		}
		
		@Override
		public File getFile() {
			return file;
		}

		@Override
		public Date getDate() {
			return date;
		}

		@Override
		public boolean isLastModified() {
			return lastModified;
		}
		
		@Override
		public boolean hasImage(){
			return hasImage;
		}

		@Override
		public BufferedImage getImage() {
			return image;
		}

		@Override
		public ImageType getType() {
			return type;
		}
	}
}