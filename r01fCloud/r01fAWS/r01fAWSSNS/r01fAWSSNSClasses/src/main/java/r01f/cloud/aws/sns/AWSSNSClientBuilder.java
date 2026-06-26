package r01f.cloud.aws.sns;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import software.amazon.awssdk.regions.Region;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AWSSNSClientBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSSNSClientBuilderCredentialsStep region(final Region region) {
		return new AWSSNSClientBuilder() {/* nothing */}
					.new AWSSNSClientBuilderCredentialsStep(region);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSSNSClientBuilderCredentialsStep {
		private final Region _region;

		public AWSSESClientBuilderBuildeStep using(final AWSAccessKey accessKey,final AWSAccessSecret accessSecret) {
			return new AWSSESClientBuilderBuildeStep(_region,
													 accessKey,accessSecret);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSSESClientBuilderBuildeStep {
		private final Region _region;
		private final AWSAccessKey _accessKey;
		private final AWSAccessSecret _accessSecret;

		public AWSSNSClient build() {
			return new AWSSNSClient(_region,
									_accessKey,_accessSecret);
		}
	}
}
