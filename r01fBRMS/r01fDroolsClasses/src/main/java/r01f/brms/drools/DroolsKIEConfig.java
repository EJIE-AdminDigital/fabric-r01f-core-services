package r01f.brms.drools;



import java.util.Collection;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.brms.drools.model.DroolsRulePath;
import r01f.brms.drools.model.DroolsRulePathSetCollection;
import r01f.brms.drools.model.DroolsServiceData;
import r01f.config.ContainsConfigData;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;


@Slf4j
@Accessors(prefix="_")
public class DroolsKIEConfig
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final DroolsServiceData _apiData;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public DroolsKIEConfig(final DroolsServiceData apiData) {	
		_apiData = apiData;
	}
	public static DroolsKIEConfig createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return DroolsKIEConfig.createFrom(xmlProps,
									     "drools");
	}
	public static DroolsKIEConfig createFrom(final XMLPropertiesForAppComponent xmlProps,
										     final String propsRootNode) {
		// ensure the root node
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "drools" : propsRootNode;

		
		// Get the drools info from the properties file
		DroolsServiceData apiData = DroolsKIEConfig.apiDataFromProperties(xmlProps,
																       thePropsRootNode);

		// return the config
		return new DroolsKIEConfig(apiData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	static DroolsServiceData apiDataFromProperties(final XMLPropertiesForAppComponent props,
											       final String propsRootNode) {		
		DroolsServiceData droolsServiceData = new DroolsServiceData();
		Collection<DroolsRulePath>	rules =	props.propertyAt(propsRootNode + "/rules")
		    	                                 .asObjectList(n -> new DroolsRulePath(n.getTextContent()));
		if (CollectionUtils.hasData(rules)) {	
			droolsServiceData.setRuleSet(new DroolsRulePathSetCollection(rules));
		} else {
			log.error(" No rule found");
		}		
		return droolsServiceData;
	}
}
