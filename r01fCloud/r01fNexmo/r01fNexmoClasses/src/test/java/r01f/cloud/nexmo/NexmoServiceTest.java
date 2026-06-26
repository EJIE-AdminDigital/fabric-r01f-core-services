package r01f.cloud.nexmo;

import r01f.cloud.nexmo.model.entities.NexmoOutboundMessageFactoryTestFactory;
import r01f.cloud.nexmo.model.outbound.NexmoOutboundMessage;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class NexmoServiceTest {
	
	public static void main(String[] argv) {
		
		
		final XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AppCode.forId("z99"))
				                                                 .notUsingCache();
		AppComponent appComponent =  AppComponent.compose("nexmo","service");
		XMLPropertiesForAppComponent propsForComponent = xmlProps.forComponent(appComponent);
		
	    Marshaller marshhaler = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                              	.build();
		
	    //API
		NexmoConfig config = NexmoConfig.createFrom(propsForComponent);
		NexmoAPI nexmoAPI = new NexmoAPI(config,marshhaler);
		
		// Test
		NexmoOutboundMessage out = new NexmoOutboundMessageFactoryTestFactory().create();
				
		nexmoAPI.getForMessageApplication()
		        .send(out.getTo(), out.getMessage());
		
	}

}
