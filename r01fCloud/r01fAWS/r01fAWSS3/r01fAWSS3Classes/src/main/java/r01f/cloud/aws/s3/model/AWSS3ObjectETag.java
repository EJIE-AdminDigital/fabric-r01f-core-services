package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;


@Immutable
public record AWSS3ObjectETag(@Getter String id)
   implements OIDTyped<String> {

	public static AWSS3ObjectETag from(final String idAsString) {
		return new AWSS3ObjectETag(idAsString);
	}
	public static AWSS3ObjectETag forId(final String idAsString) {
		return new AWSS3ObjectETag(idAsString);
	}
	public static AWSS3ObjectETag valueOf(final String idAsString) {
		return new AWSS3ObjectETag(idAsString);
	}
	public static AWSS3ObjectETag fromString(final String idAsString) {
		return new AWSS3ObjectETag(idAsString);
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