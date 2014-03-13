package photosorter.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class FileUtility {

	public static String getFileExtension(File file){
		if(file.isDirectory() || !file.getName().contains("."))
			throw new IllegalArgumentException();
		return file.getName().substring(file.getName().lastIndexOf('.')).toLowerCase();
	}
	
	public static String getFileName(File file){
		if(file.isDirectory() || !file.getName().contains("."))
			throw new IllegalArgumentException();
		return file.getName().substring(0, file.getName().lastIndexOf('.'));
	}
	
	public static int fileCount(File file, FileFilter filter){
		if(file.isDirectory()){
			File[] files = file.listFiles(filter);
			if(files == null)
				return 0;
			int numFiles = files.length;
			for(File f : files)
				if(f.isDirectory())
					numFiles += (fileCount(f, filter) - 1);
			return numFiles;
		} else
			return 1;
	}
	
	public static File openFileChooser(JComponent parent){
		return openFileChooser(parent, JFileChooser.FILES_ONLY);
	}
	
	public static File openFileChooser(JComponent parent, int mode){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(mode);
		int choice = fc.showOpenDialog(parent);
		if(choice == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		return null;
	}
	
	public static File saveFileChooser(JComponent parent){
		return saveFileChooser(parent, JFileChooser.FILES_ONLY);
	}
	
	public static File saveFileChooser(JComponent parent, int mode){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(mode);
		int choise = fc.showSaveDialog(parent);
		if(choise == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		return null;
	}
	
	public static void copyFile(File source, File destination) throws IOException{
		cpFile(source, destination);
		destination.setLastModified(source.lastModified());
	}
	
	public static void copyFile(File source, File destination, Date date) throws IOException{
		cpFile(source, destination);
		destination.setLastModified(date.getTime());
	}
	
	private static void cpFile(File source, File destination) throws IOException{
		if(!destination.exists())
			destination.createNewFile();
		FileChannel fcSource = null, fcDestination = null;
		try{
			fcSource = new FileInputStream(source).getChannel();
			fcDestination = new FileOutputStream(destination).getChannel();
			fcDestination.transferFrom(fcSource, 0, fcSource.size());
		} finally {
			if(fcSource != null)
				fcSource.close();
			if(fcDestination != null)
				fcDestination.close();
		}
	}
	
	public static String newFileName(File parent, String baseName, String ext, int start){
		final String duplicateTag = "_$$$_";
		File file = new File(parent, baseName + duplicateTag + start + ext);
		if(file.exists())
			return newFileName(parent, baseName, ext, ++start);
		return baseName + duplicateTag + start + ext;
	}
}
