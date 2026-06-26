package r01f.brms.drools.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public class DroolsServiceData {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="ruleSet",
			   	   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private DroolsRulePathSetCollection _ruleSet;
}
