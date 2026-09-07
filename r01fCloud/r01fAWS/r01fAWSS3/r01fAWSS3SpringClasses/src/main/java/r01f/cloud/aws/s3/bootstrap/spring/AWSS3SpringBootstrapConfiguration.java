package r01f.cloud.aws.s3.bootstrap.spring;



import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfig;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigSet;
import r01f.cloud.aws.s3.client.api.AWSS3ClientConfigSet.AWSS3ClientConfigItem;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.regions.Region;


@Accessors(prefix="_")
public class AWSS3SpringBootstrapConfiguration 
  implements Serializable,
  			 Debuggable {

	private static final long serialVersionUID = 6842734543453332123L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final String PROPERTIES_PREFIX = "s3";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter @Setter private Collection<AWSS3SpringBootstrapConfigurationItem> _buckets = new LinkedHashSet<>();

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3SpringBootstrapConfiguration() {
		// default no args constructor
	}

	public AWSS3ClientConfigSet toClientConfigSet() {	
		// From Spring Config structure (old flat yml item with region) to new structured S3 config set
		if (_buckets == null || _buckets.isEmpty()) {
			return new AWSS3ClientConfigSet();
		}

		// Stream, map, and collect elements from the raw properties collection
		Collection<AWSS3ClientConfigItem> items =
				_buckets.stream()
							  .map(springConfigItem -> {
								  // 1. Credentials-
								  AWSAccessKey accessKey = AWSAccessKey.forId(springConfigItem.getAccessKey().asString());
								  AWSAccessSecret accessSecret = AWSAccessSecret.forId(springConfigItem.getAccessSecret().asString());
								  
								  // 2. Regio, this is very important for bucket discovering!							
								  String regionStr = springConfigItem.getRegionId() != null ? springConfigItem.getRegionId() : "eu-west-1";
								  Region region = Region.of(regionStr);
								  
								  // 3. SS3  configIt 
								  AWSS3ClientConfig configIt = new AWSS3ClientConfig( region, 
										                                              accessKey,
										                                              accessSecret
								  );
								  configIt.setEndPoint(springConfigItem.getEndPointUrl());
								  // 4. Devolvemos el ítem con su jerarquía anidada e ID de bucket correspondiente
								  return new AWSS3ClientConfigItem( springConfigItem.getBucketId(), 
										                            configIt
								  );
							  })
							  .collect(Collectors.toList());

		return new AWSS3ClientConfigSet(items);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	CONVENIENCE METHODS FOR API PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Devuelve el AWSS3ClientConfig técnico buscando por el ID del bucket dentro de la colección.
	 */
	/*public AWSS3ClientConfig getConfigFor(final AWSS3Bucket bucket) {
		if (CollectionUtils.isNullOrEmpty(_clientConfigs) 
				|| bucket == null) return null;
		
		return _clientConfigs.stream()
								 .filter(item -> bucket.equals(item.getBucketId()))
								 .map(AWSS3ClientConfigItem::getConfig)
								 .findFirst()
							 .orElse(null);
	}*/
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("AWSS3SpringBootstrapConfiguration [Registered Buckets=").append(CollectionUtils.sizeOf(_buckets)).append("]\n");
		if (CollectionUtils.hasData(_buckets)) {
			for (AWSS3SpringBootstrapConfigurationItem item : _buckets) {
				sb.append("  - ").append(item.debugInfo()).append("\n");
			}
		}
		return sb.toString();
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//INNER STATIC ITEM
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public  static class  AWSS3SpringBootstrapConfigurationItem 
		 implements Serializable,
			        Debuggable {
	
		private static final long serialVersionUID = -4823948239482938492L;
	/////////////////////////////////////////////////////////////////////////////////////////
	//FIELDS
	/////////////////////////////////////////////////////////////////////////////////////////	
		@MarshallField(as="accessKey")
		@Getter @Setter private SecurityToken _accessKey;
		
		@MarshallField(as="accessSecret")
		@Getter @Setter private Password _accessSecret;  
		
		@MarshallField(as="endPointUrl")
		@Getter @Setter private Url _endPointUrl;
		
		@MarshallField(as="bucketId")
		@Getter @Setter private AWSS3BucketID _bucketId;
		
		@MarshallField(as="regionId")
		@Getter @Setter private String _regionId;
		/////////////////////////////////////////////////////////////////////////////////////////
		//CONSTRUCTORS
		/////////////////////////////////////////////////////////////////////////////////////////
		public  AWSS3SpringBootstrapConfigurationItem() {
			// default no args constructor for marshalling
		}
		@Override
		public CharSequence debugInfo() {
			StringBuilder sb = new StringBuilder();
			sb.append("[S3 Endpoint Configuration Item]").append("\n");
			sb.append("  - Bucket ID:    ").append(_bucketId != null ? _bucketId.asString() : "NULL").append("\n");
			sb.append("  - Region ID:    ").append(_regionId != null ? _regionId : "NOT DEFINED (Fallback to eu-west-1)").append("\n");
			sb.append("  - Endpoint URL: ").append(_endPointUrl != null ? _endPointUrl.asString() : "Default AWS Cloud URL").append("\n");
			sb.append("  - Access Key:   ").append(_accessKey != null ? _accessKey.asString() : "NULL").append("\n");
			// Por seguridad, solo confirmamos si el secret está presente o no, nunca lo pintamos en plano
			sb.append("  - Access Sec:   ").append(_accessSecret != null ? "[PROTECTED / PRESENT]" : "NULL");
			return sb.toString();
		}
	}
	
	
}