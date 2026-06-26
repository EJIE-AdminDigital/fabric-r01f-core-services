package r01f.messaging.rabbitmq.test.model;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.messaging.rabbitmq.test.model.MyOIDs.MyTestOID;
import r01f.model.PersistableModelObjectBase;
import r01f.types.Path;
import r01f.types.url.Url;


@Accessors(prefix="_")
public class MyTestModelObject
     extends PersistableModelObjectBase<MyTestOID,MyTestModelObject> {

	private static final long serialVersionUID = -2412005175445501481L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private String _name;
	@Getter @Setter private Language _lang;
	@Getter @Setter private Url _url;
	@Getter @Setter private Path _path;
	@Getter @Setter private LanguageTexts _description;
	@Getter @Setter private Collection<String> _col;
	@Getter @Setter private Map<Integer,String> _map;

}
