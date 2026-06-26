package r01f.brms.drools.entities;

import r01f.brms.drools.model.DroolsRulePath;
import r01f.brms.drools.model.DroolsRulePathSetCollection;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.patterns.Factory;

public class DroolsRulePathSetCollectionFactory
  implements Factory<DroolsRulePathSetCollection> {

	@Override
	public DroolsRulePathSetCollection create() {
		return _buildOutboundMessage();
	}

	private static DroolsRulePathSetCollection _buildOutboundMessage() {
	
		DroolsRulePath  rule1  = new DroolsRulePath("com/baeldung/drools/rules/BackwardChaining.drl");
		DroolsRulePath  rule2  = new DroolsRulePath("com/baeldung/drools/rules/BackwardChaining2.drl");
		DroolsRulePath  rule3  = new DroolsRulePath("com/baeldung/drools/rules/BackwardChaining3.drl");
		return new DroolsRulePathSetCollection(rule1,rule2,rule3);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] argv) {
	    String json = MarshallerBuilder.findTypesToMarshallAt(R01FAppCodes.APP_CODE)
                                     	.build()
                                     .forWriting()
								     .toXml(new DroolsRulePathSetCollectionFactory().create());

	    System.out.println(json);

	}
}
