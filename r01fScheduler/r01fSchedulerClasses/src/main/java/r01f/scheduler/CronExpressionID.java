package r01f.scheduler;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="cronExpressionId")
@NoArgsConstructor
public final class CronExpressionID
	 	   extends OIDBaseMutable<String>  {
	
	private static final long serialVersionUID = -9181977752174509450L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public CronExpressionID(final String oid) {
		super(oid);
	}
	public static CronExpressionID valueOf(final String id) {
		return new CronExpressionID(id);
	}
	public static CronExpressionID forId(final String id) {
		return new CronExpressionID(id);
	}
}