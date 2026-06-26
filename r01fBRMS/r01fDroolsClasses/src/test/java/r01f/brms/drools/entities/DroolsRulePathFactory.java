package r01f.brms.drools.entities;

import r01f.brms.drools.model.DroolsRulePath;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;

public class DroolsRulePathFactory
  implements Factory<DroolsRulePath> {
	
	@Override
	public DroolsRulePath create() {
		return _buildOutboundMessage();
	}
	
	
	private static DroolsRulePath _buildOutboundMessage() {
		DroolsRulePath  rule  = new DroolsRulePath("com/baeldung/drools/rules/BackwardChaining.drl");
	
	
		return rule;		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] argv) {
	    String json = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                                     	.build()
                                     .forWriting()
								     .toJson(new DroolsRulePathFactory().create());
	    
	    System.out.println(json); 
		
	}
}
