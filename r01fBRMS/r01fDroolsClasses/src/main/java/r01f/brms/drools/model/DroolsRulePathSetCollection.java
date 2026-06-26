package r01f.brms.drools.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public class DroolsRulePathSetCollection
		   implements Iterable<DroolsRulePath> {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="ruleSet",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private Set<DroolsRulePath> _set;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public DroolsRulePathSetCollection() {	
	}
	public DroolsRulePathSetCollection(final Set<DroolsRulePath> symbols) {
		_set = symbols;
	}
	public DroolsRulePathSetCollection(final Iterable<DroolsRulePath> symbols) {
		_set = Sets.newLinkedHashSet(symbols);
	}
	public DroolsRulePathSetCollection(final DroolsRulePath...rule) {
		_set = new HashSet<>(Arrays.asList(rule));
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<DroolsRulePath> iterator() {
		return _set != null ? _set.iterator()
							: Lists.<DroolsRulePath>newArrayList().iterator();
	}
}
