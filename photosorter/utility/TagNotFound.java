package photosorter.utility;

import com.drew.metadata.Metadata;

@SuppressWarnings("serial")
public class TagNotFound extends Exception{
	private final Metadata meta;

	public TagNotFound(Metadata meta) {
		this.meta = meta;
	}
	
	public Metadata getMeta(){
		return meta;
	}
}