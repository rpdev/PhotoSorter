package photosorter;

public interface ImageObserver {
	void imageUpdate(final String tag, final DataPackage data);
	void imageRemoved(final String tag, final DataPackage data);
}
