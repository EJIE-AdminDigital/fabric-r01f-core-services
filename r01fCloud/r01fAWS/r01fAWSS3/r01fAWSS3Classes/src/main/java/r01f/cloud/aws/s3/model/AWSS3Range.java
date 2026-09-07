package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;


@Immutable
public record AWSS3Range(@Getter String id)
   implements OIDTyped<String> {

	public static AWSS3Range from(final String idAsString) {
		return new AWSS3Range(idAsString);
	}
	public static AWSS3Range forId(final String idAsString) {
		return new AWSS3Range(idAsString);
	}
	public static AWSS3Range valueOf(final String idAsString) {
		return new AWSS3Range(idAsString);
	}
	public static AWSS3Range fromString(final String idAsString) {
		return new AWSS3Range(idAsString);
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