package r01f.scheduler;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="cronExpressionId")
public record CronExpressionID(@Getter String id)
   implements OIDTyped<String>  {
	
	public static CronExpressionID forId(final String id) {
		return new CronExpressionID(id);
	}
	public static CronExpressionID from(final String id) {
		return new CronExpressionID(id);
	}
	public static CronExpressionID valueOf(final String id) {
		return new CronExpressionID(id);
	}
	public static CronExpressionID fromString(final String id) {
		return new CronExpressionID(id);
	}
	
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}