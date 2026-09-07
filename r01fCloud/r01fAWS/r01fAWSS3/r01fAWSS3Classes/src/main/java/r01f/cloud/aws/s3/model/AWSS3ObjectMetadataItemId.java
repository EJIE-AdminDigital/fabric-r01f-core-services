package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;


@Immutable
public record AWSS3ObjectMetadataItemId(@Getter String id)
   implements OIDTyped<String> {

	public static AWSS3ObjectMetadataItemId from(final String idAsString) {
		return new AWSS3ObjectMetadataItemId(idAsString);
	}
	public static AWSS3ObjectMetadataItemId forId(final String idAsString) {
		return new AWSS3ObjectMetadataItemId(idAsString);
	}
	public static AWSS3ObjectMetadataItemId valueOf(final String idAsString) {
		return new AWSS3ObjectMetadataItemId(idAsString);
	}
	public static AWSS3ObjectMetadataItemId fromString(final String idAsString) {
		return new AWSS3ObjectMetadataItemId(idAsString);
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