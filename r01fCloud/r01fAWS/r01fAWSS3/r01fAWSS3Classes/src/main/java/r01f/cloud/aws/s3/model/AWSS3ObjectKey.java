package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.types.Path;


@Immutable
public record AWSS3ObjectKey(@Getter String id)
   implements OIDTyped<String> {

	public static AWSS3ObjectKey from(final String idAsString) {
		return new AWSS3ObjectKey(idAsString);
	}
	public static AWSS3ObjectKey forId(final String idAsString) {
		return new AWSS3ObjectKey(idAsString);
	}
	public static AWSS3ObjectKey valueOf(final String idAsString) {
		return new AWSS3ObjectKey(idAsString);
	}
	public static AWSS3ObjectKey fromString(final String idAsString) {
		return new AWSS3ObjectKey(idAsString);
	}
	public static AWSS3ObjectKey of(final Path path) {
		return new AWSS3ObjectKey(path.asAbsoluteString());
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