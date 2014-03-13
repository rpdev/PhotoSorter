package photosorter;

public interface PhotoObserver {
	void copyingFile(String from, String to, DataPackage data);
	void collitionFile(DataPackage collitionWith, DataPackage newData);
	
	void fileCountUpdate(CountType type, int count);
	enum CountType{TOTAL, SINGEL};
	
	void imageUpdate(final String tag, final DataPackage data);
	void imageRemoved(final String tag, final DataPackage data);
}
