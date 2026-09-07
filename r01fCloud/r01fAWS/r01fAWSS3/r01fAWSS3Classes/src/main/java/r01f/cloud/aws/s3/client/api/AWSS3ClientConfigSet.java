package r01f.cloud.aws.s3.client.api;



import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="s3ClientConfigSet")
@Accessors(prefix="_")
public class AWSS3ClientConfigSet 
  implements Serializable {

	private static final long serialVersionUID = 8492834923849238492L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="items")
	@Getter @Setter private Collection<AWSS3ClientConfigItem> _items = new LinkedHashSet<>();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientConfigSet() {		
	}

	public AWSS3ClientConfigSet(final Collection<AWSS3ClientConfigItem> items) {
		if (CollectionUtils.hasData(items)) {
			_items.addAll(items);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * GetConfigFor
	 */
	public AWSS3ClientConfig getConfigFor(final AWSS3BucketID bucket) {
		if (CollectionUtils.isNullOrEmpty(_items) || bucket == null) return null;
		
		return _items.stream()
						 .filter(item -> bucket.equals(item.getBucketId()))
						 .map(AWSS3ClientConfigItem::getConfig)
						 .findFirst()
					 .orElse(null);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	 INNER STATIC ITEM
/////////////////////////////////////////////////////////////////////////////////////////
	public  static class AWSS3ClientConfigItem 
	  	implements Serializable{

		private static final long serialVersionUID = -4823948239482938492L;
	/////////////////////////////////////////////////////////////////////////////////////////
//		FIELDS
	/////////////////////////////////////////////////////////////////////////////////////////
		@MarshallField(as="bucketId")
		@Getter @Setter private AWSS3BucketID _bucketId;

		@MarshallField(as="config")
		@Getter @Setter private AWSS3ClientConfig _config;
	/////////////////////////////////////////////////////////////////////////////////////////
//		CONSTRUCTORS
	/////////////////////////////////////////////////////////////////////////////////////////
		public AWSS3ClientConfigItem() {
			// default no args constructor for marshalling
		}

		public AWSS3ClientConfigItem(final AWSS3BucketID bucketId, final AWSS3ClientConfig config) {
			_bucketId = bucketId;
			_config = config;
		}
	}
}