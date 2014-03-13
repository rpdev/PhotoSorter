package photosorter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import photosorter.FileCountObserver.CountType;
import photosorter.utility.FileUtility;
import photosorter.utility.LogFile;

class CopyPhoto{
	private final ArrayList<Thread> threads = new ArrayList<Thread>();
	private final ArrayList<CopyFileObserver> copyObs = new ArrayList<CopyFileObserver>();
	private final ArrayList<FileCountObserver> countObs = new ArrayList<FileCountObserver>();
	private boolean stop = false, done = false;
	private int count = 0;
	private Thread o, c;
	
	CopyPhoto(final TreeMap<String, ArrayList<DataPackage>> treeMap, final File destination) {
		for(ArrayList<DataPackage> d : treeMap.values())
			count += d.size();
		
		if(destination.isFile())
			throw new IllegalArgumentException("Destination is a file");
		collitionTest(treeMap, destination);
		Thread t = new Thread(){
			@Override
			public void run(){
				end: for(Entry<String, ArrayList<DataPackage>> f : treeMap.entrySet()){
					File dir = new File(destination, f.getKey());
					if(!dir.exists() || dir.isFile())
						dir.mkdir();
					for(DataPackage d : f.getValue()){
						if(stop)
							break end;
						File file = new File(dir, d.getFile().getName());
						if(file.exists())
							file = new File(dir, FileUtility.newFileName(dir, FileUtility.getFileName(file), FileUtility.getFileExtension(file), 1));
						notifyCopyObs(d.getFile().getAbsolutePath(), file.getAbsolutePath(), d);
						try {
							FileUtility.copyFile(d.getFile(), file, d.getDate());
						} catch (IOException e) {
							LogFile.writeExceptionToLog(e);
						}
					}
				}
				if(c != null)
					c.notify();
				else if(o != null){
					synchronized (o) {
						o.notify();
					}
				} else
					done = true;
			}
		};
		threads.add(t);

	}
	
	CopyPhoto(TreeMap<String, ArrayList<DataPackage>> treeMap, File destFile, final ArrayList<DataPackage> removed, final File destRemoveFile) {
		this(treeMap, destFile);
		collitionTest(removed, destRemoveFile);
		count += removed.size();
		Thread t = new Thread(){
			@Override
			public void run(){
				o = this;
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for(DataPackage d : removed){
					if(stop)
						break;
					File file = new File(destRemoveFile, d.getFile().getName());
					if(file.exists())
						file = new File(destRemoveFile, FileUtility.newFileName(destRemoveFile, file.getName(), FileUtility.getFileExtension(file), 1));
					notifyCopyObs(d.getFile().getAbsolutePath(), file.getAbsolutePath(), d);
					try {
						FileUtility.copyFile(d.getFile(), file, d.getDate());
					} catch (IOException e) {
						LogFile.writeExceptionToLog(e);
					}
				}
				if(c != null)
					c.notify();
				done = true;
			}
		};
		threads.add(t);
	}
	
	void registerCountObs(FileCountObserver ob){
		countObs.add(ob);
	}
	
	boolean isDone(){
		return done;
	}
	
	void start(){
		notifyCountObs(CountType.TOTAL, count);
		count = 0;
		for(Thread t : threads)
			t.start();
	}
	
	private void notifyCountObs(CountType c, int i){
		for(FileCountObserver o : countObs)
			o.fileCountUpdate(c, i);
	}
	
	private void notifyCopyObs(String from, String to, DataPackage data){
		notifyCountObs(CountType.SINGEL, ++count);
		for(CopyFileObserver o : copyObs)
			o.copyingFile(from, to, data);
	}

	void registerCopyFileObserver(CopyFileObserver ob) {
		copyObs.add(ob);
	}

	void stop() {
		stop = true;
		c = Thread.currentThread();
		synchronized (c) {
			try {
				c.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void collitionTest(TreeMap<String, ArrayList<DataPackage>> files, File destination){
		for(Entry<String, ArrayList<DataPackage>> e : files.entrySet()){
			File dir = new File(destination, e.getKey());
			if(dir.exists() && dir.isDirectory())
				collitionTest(e.getValue(), dir);
		}
	}
	
	private void collitionTest(ArrayList<DataPackage> files, File destination){
		for(DataPackage p : files){
			File f = new File(destination, p.getFile().getName());
		}
	}
}
