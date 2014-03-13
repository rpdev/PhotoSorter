package photosorter;

public interface CopyFileObserver {
	void copyingFile(String from, String to, DataPackage data);
}
