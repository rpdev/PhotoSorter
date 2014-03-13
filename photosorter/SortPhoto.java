package photosorter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import photosorter.ScanPhoto.ImageUpdate;

class SortPhoto implements ImageUpdate{
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM (MMM)");
	private final ArrayList<DataPackage> deletedFiles = new ArrayList<DataPackage>();
	private final ArrayList<DataPackage> lastModList = new ArrayList<DataPackage>();
	private final ArrayList<ImageObserver> imageObservers = new ArrayList<ImageObserver>();
	private final TreeMap<String, ArrayList<DataPackage>> files = new TreeMap<String, ArrayList<DataPackage>>(new Comparator<String>() {
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
	
	SortPhoto() {
		;
	}
	
	TreeMap<String, ArrayList<DataPackage>> getFiles(){
		return files;
	}
	
	ArrayList<DataPackage> getRemovedFiles(){
		return deletedFiles;
	}
	
	@Override
	public void newImage(DataPackage data) {
		String key = dateFormatter.format(data.getDate());
		if(!files.containsKey(key))
			files.put(key, new ArrayList<DataPackage>());
		files.get(key).add(data);
		if(data.isLastModified() && !lastModList.contains(data))
			lastModList.add(data);
		
		notifyObserversNew(key, data);
	}
	
	void registerImageObserver(ImageObserver ob){
		imageObservers.add(ob);
	}
	
	void addDataPackage(DataPackage data){
		if(deletedFiles.remove(data))
			newImage(data);
	}
	
	void removeDataPackage(DataPackage data){
		if(files.get(dateFormatter.format(data.getDate())).remove(data)){
			deletedFiles.add(data);
			notifyObserversRemoved(dateFormatter.format(data.getDate()), data);
		}
	}
	
	private void notifyObserversNew(String tag, DataPackage data){
		for(ImageObserver o : imageObservers)
			o.imageUpdate(tag, data);
	}
	
	private void notifyObserversRemoved(String tag, DataPackage data){
		for(ImageObserver o : imageObservers)
			o.imageRemoved(tag, data);
	}
}
