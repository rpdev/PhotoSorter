package photosorter.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class PhotoUtility {
	private final static Map<Class<? extends Directory>, Integer[]> dateMeta;
	static{
		Map<Class<? extends Directory>, Integer[]> map = new HashMap<Class<? extends Directory>, Integer[]>();
		map.put(ExifSubIFDDirectory.class,
				new Integer[]{ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL}
		);
		/*map.put(ExifIFD0Directory.class,
			new Integer[]{ExifIFD0Directory.TAG_DATETIME}
		);*/
		dateMeta = Collections.unmodifiableMap(map);
	}
	
	public static Date[] getData(Metadata meta) throws TagNotFound{
		ArrayList<Date> dates = new ArrayList<Date>();
		for(Map.Entry<Class<? extends Directory>, Integer[]> e : dateMeta.entrySet()){
			if(!meta.containsDirectory(e.getKey()))
				continue;
			Directory dir = meta.getDirectory(e.getKey());
			for(int tag : e.getValue()){
				if(!dir.containsTag(tag))
					continue;
				dates.add(dir.getDate(tag));
			}
		}
		if(dates.size() > 0)
			return dates.toArray(new Date[dates.size()]);
		throw new TagNotFound(meta);
	}
}
