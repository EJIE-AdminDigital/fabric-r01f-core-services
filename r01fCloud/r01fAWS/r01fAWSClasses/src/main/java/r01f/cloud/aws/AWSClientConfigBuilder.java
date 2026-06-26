package r01f.cloud.aws;

import java.nio.charset.Charset;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.FactoryFrom;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import software.amazon.awssdk.regions.Region;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
public abstract class AWSClientConfigBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSClientConfig fromXMLProperties(final XMLPropertiesForAppComponent props,
														  final String propsRootNode,final AWSService awsService) {
		////////// service independent properties
		Region region = props.propertyAt(propsRootNode + "/aws/region")
					  	 	 .asObjectFromString(new FactoryFrom<String,Region>() {
														@Override
														public Region from(final String reg) {
															return Region.of(reg);
														}
					  	 	 					 },
					  	 			 			 Region.EU_WEST_1);
		Charset charset = props.propertyAt(propsRootNode + "/aws/charset")
									  .asObjectFromString(new FactoryFrom<String,Charset>() {
																@Override
																public Charset from(final String charset) {
																	return Charset.forName(charset);
																}
														  },
											  			  Charset.defaultCharset());
		////////// service dependent properties
		AWSAccessKey key = props.propertyAt(propsRootNode + "/aws/" + awsService.nameLowerCase() + "/accessKey")
								  .asObjectFromString(new FactoryFrom<String,AWSAccessKey>() {
															@Override
															public AWSAccessKey from(final String key) {
																return AWSAccessKey.forId(key);
															}
													  },
										  			  AWSAccessKey.forId("--KEY NOT FOUND--"));
		AWSAccessSecret secret = props.propertyAt(propsRootNode + "/aws/" + awsService.nameLowerCase() + "/accessSecret")
									  .asObjectFromString(new FactoryFrom<String,AWSAccessSecret>() {
																@Override
																public AWSAccessSecret from(final String key) {
																	return AWSAccessSecret.forId(key);
																}
														  },
											  			  AWSAccessSecret.forId("--SECRET NOT FOUND--"));

		////////// HttpSettings
		AWSClientHttpSettings httpSettings = AWSClientHttpSettingsBuilder.fromXMLProperties(props, propsRootNode, awsService);

		if (key == null || secret == null) {
			throw new IllegalStateException("AWS SES key/secret pair is mandatory!");
		}
		// return
		return new AWSClientConfig(region,
							       key,secret,
							       charset,
								   httpSettings);
	}
	public static AWSClientConfigBuilderCredentialsStep region(final Region region) {
		return new AWSClientConfigBuilder() {/* nothing */}
					.new AWSClientConfigBuilderCredentialsStep(region);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSClientConfigBuilderCredentialsStep {
		private final Region _region;

		public AWSClientConfigBuilderCharsetStep using(final AWSAccessKey accessKey,final AWSAccessSecret accessSecret) {
			return new AWSClientConfigBuilderCharsetStep(_region,
													  	 accessKey,accessSecret);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSClientConfigBuilderCharsetStep {
		private final Region _region;
		private final AWSAccessKey _accessKey;
		private final AWSAccessSecret _accessSecret;

		public AWSClientConfigBuilderBuildeStep charset(final Charset charset) {
			return new AWSClientConfigBuilderBuildeStep(_region,
													 	_accessKey,_accessSecret,
													 	charset);
		}
		public AWSClientConfigBuilderBuildeStep defaultCharset() {
			return this.charset(Charset.defaultCharset());
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSClientConfigBuilderBuildeStep {
		private final Region _region;
		private final AWSAccessKey _accessKey;
		private final AWSAccessSecret _accessSecret;
		private final Charset _charset;

		public AWSClientConfig build() {
			return new AWSClientConfig(_region,
									   _accessKey,_accessSecret,
									   _charset);
		}
	}
}
