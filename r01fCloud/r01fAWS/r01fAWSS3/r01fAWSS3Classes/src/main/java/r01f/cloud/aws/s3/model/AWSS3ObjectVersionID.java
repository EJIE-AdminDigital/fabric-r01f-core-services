package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.guids.VersionID;


@Immutable
public record AWSS3ObjectVersionID(@Getter String id)
   implements OIDTyped<String>,
  			  VersionID {
	public static AWSS3ObjectVersionID from(final String idAsString) {
		return new AWSS3ObjectVersionID(idAsString);
	}
	public static AWSS3ObjectVersionID forId(final String idAsString) {
		return new AWSS3ObjectVersionID(idAsString);
	}
	public static AWSS3ObjectVersionID valueOf(final String idAsString) {
		return new AWSS3ObjectVersionID(idAsString);
	}
	public static AWSS3ObjectVersionID fromString(final String idAsString) {
		return new AWSS3ObjectVersionID(idAsString);
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