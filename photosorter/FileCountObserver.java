package photosorter;

public interface FileCountObserver {
	void fileCountUpdate(CountType type, int count);
	enum CountType{TOTAL, SINGEL};
}
