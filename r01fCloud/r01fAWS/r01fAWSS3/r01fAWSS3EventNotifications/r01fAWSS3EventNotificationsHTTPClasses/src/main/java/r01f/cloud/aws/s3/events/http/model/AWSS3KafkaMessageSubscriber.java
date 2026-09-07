package r01f.cloud.aws.s3.events.http.model;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.cloud.aws.s3.events.model.AWSS3EventServiceSubscriber;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * 
 */
@Immutable

@Accessors(prefix="_")
@MarshallType(as="httpMessageSubscriber")
public  interface AWSS3KafkaMessageSubscriber
			extends AWSS3EventServiceSubscriber  {

/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Callback triggered when a message is successfully received and parsed.
	 * * @param key The identifier of the model object (OID)
	 * @param message The actual model object payload
	 */
	public <O extends OID, M extends ModelObject & HasOID<O>> void onMessage(final O key, final M message);

	/**
	 * Callback triggered when an unrecoverable error occurs during 
	 * the reception or deserialization phase.
	 */
	public void onError(final Throwable throwable);
}