package photosorter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

public interface DataPackage {
	File getFile();
	Date getDate();
	ImageType getType();
	boolean hasImage();
	boolean isLastModified();
	BufferedImage getImage();
	
	enum ImageType { JPG, PNG, GIF, BMP }
}
