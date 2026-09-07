package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;


@Immutable
public record AWSS3RequestID(@Getter String id)
   implements OIDTyped<String> {

	public static AWSS3RequestID from(final String idAsString) {
		return new AWSS3RequestID(idAsString);
	}
	public static AWSS3RequestID forId(final String idAsString) {
		return new AWSS3RequestID(idAsString);
	}
	public static AWSS3RequestID valueOf(final String idAsString) {
		return new AWSS3RequestID(idAsString);
	}
	public static AWSS3RequestID fromString(final String idAsString) {
		return new AWSS3RequestID(idAsString);
	}
	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}