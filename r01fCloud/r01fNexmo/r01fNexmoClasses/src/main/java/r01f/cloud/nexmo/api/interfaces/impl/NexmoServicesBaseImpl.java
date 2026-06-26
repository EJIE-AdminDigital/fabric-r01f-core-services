package r01f.cloud.nexmo.api.interfaces.impl;

import com.nexmo.client.NexmoClient;

import r01f.cloud.nexmo.NexmoAPI.NexmoAPIData;
import r01f.objectstreamer.Marshaller;

abstract class NexmoServicesBaseImpl {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	 protected final NexmoClient _nexmoClient;
	 protected final NexmoAPIData _apiData;
	 protected final Marshaller _marshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public NexmoServicesBaseImpl( final NexmoAPIData apiData,
			                      final NexmoClient nexmoClient, 
			                      final Marshaller marshaller) {
		_nexmoClient = nexmoClient;
		_apiData = apiData;
		_marshaller = marshaller;
	}
}
