package r01f.brms.drools.sample;

import r01f.brms.drools.DroolsKIEConfig;
import r01f.brms.drools.DroolsKIESessionService;
import r01f.brms.drools.DroolsKIESessionServiceFilesystemImpl;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class SuggestSample {
	
	
	public static DroolsKIESessionService buildKIEService() {
		final XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AppCode.forId("z99"))
                .notUsingCache();
		AppComponent appComponent =  AppComponent.compose("drools","service");
		XMLPropertiesForAppComponent propsForComponent = xmlProps.forComponent(appComponent);
		DroolsKIEConfig config = DroolsKIEConfig.createFrom(propsForComponent);
		DroolsKIESessionService kieSessionService =  new DroolsKIESessionServiceFilesystemImpl(config);
		return kieSessionService;
	}
	
	
	public static void main(String[] argv) {
		DroolsKIESessionService kieSessionService =  buildKIEService() ;
		SuggestService suggestService = new SuggestService(kieSessionService.newKieSession());
	
		OutboundSuggest outBound = suggestService.suggest(new InboundInput("Ayudas"));
		System.out.println("....outbound: " + outBound.getText());
		
		outBound = suggestService.suggest(new InboundInput(" No sabe no contesta"));
		System.out.println("....outbound: " + outBound.getText());
		
	}

}
