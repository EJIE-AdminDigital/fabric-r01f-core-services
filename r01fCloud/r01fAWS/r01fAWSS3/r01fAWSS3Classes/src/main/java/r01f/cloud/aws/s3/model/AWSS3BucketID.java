package r01f.cloud.aws.s3.model;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OID;


@Immutable
public record AWSS3BucketID(@Getter String id)
   implements IsAWSS3BucketID {

	public static AWSS3BucketID from(final String idAsString) {
		return new AWSS3BucketID(idAsString);
	}
	public static AWSS3BucketID forId(final String idAsString) {
		return new AWSS3BucketID(idAsString);
	}
	public static AWSS3BucketID valueOf(final String idAsString) {
		return new AWSS3BucketID(idAsString);
	}
	public static AWSS3BucketID fromString(final String idAsString) {
		return new AWSS3BucketID(idAsString);
	}
	public static <O extends OID> AWSS3BucketID of(final O oid) {
		return new AWSS3BucketID(oid.asString());
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