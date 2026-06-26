package r01f.cloud.aws;

public enum AWSService {
	SES,		// Simple email service
	SNS,        // Simple notification service
	S3;         // S3 Store

	public String nameLowerCase() {
		return this.name().toLowerCase();
	}
}
