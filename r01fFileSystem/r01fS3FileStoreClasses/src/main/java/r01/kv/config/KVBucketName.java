package r01.kv.config;

import lombok.Getter;
import r01f.cloud.aws.s3.model.AWSS3BucketID;
import r01f.cloud.aws.s3.model.IsAWSS3BucketID;

public record KVBucketName(@Getter String id)
   implements IsAWSS3BucketID {

	public static KVBucketName from(final String idAsString) {
		return new KVBucketName(idAsString);
	}
	public static KVBucketName forId(final String idAsString) {
		return new KVBucketName(idAsString);
	}
	public static KVBucketName valueOf(final String idAsString) {
		return new KVBucketName(idAsString);
	}
	public static KVBucketName fromString(final String idAsString) {
		return new KVBucketName(idAsString);
	}		
	public AWSS3BucketID asS3() {
		  return AWSS3BucketID.forId(this.asString());
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