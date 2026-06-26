package r01f.cloud.aws.s3.client.api;

import static software.amazon.awssdk.http.SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.cloud.aws.s3.client.api.delegates.AWSS3ClientAPIDelegateForBuckets;
import r01f.cloud.aws.s3.client.api.delegates.AWSS3ClientAPIDelegateForFiler;
import r01f.cloud.aws.s3.client.api.delegates.AWSS3ClientAPIDelegateForObjects;
import r01f.exceptions.Throwables;
import r01f.util.types.Strings;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;


/**
 * Base type for every API implementation of S3API
 */
@Slf4j
@Accessors(prefix="_")
public class AWSS3ClientAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private final S3Client _s3Client;

	@SuppressWarnings("unused")
	private final Charset _charset;
	/**
	 * BUCKETS SUB-APIs (created at the constructor)
	 */
	@Getter private final AWSS3ClientAPIDelegateForBuckets _forBuckets;
	/**
	 * S3 OBJECTS SUB-APIs (created at the constructor)
	 */
	@Getter private final AWSS3ClientAPIDelegateForObjects _forObjects;
	/**
	 * S3 FOLDER FILER SUB-APIs (created at the constructor)
	 */
	@Getter private final AWSS3ClientAPIDelegateForFiler  _forFiler;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private AWSS3ClientAPI(final Region region,	// ie: Region.EU_WEST_1
				           final AWSAccessKey accessKey,final AWSAccessSecret accessSecret,
				           final Charset charset) {
		this(new AWSS3ClientConfig(region,
								   accessKey,accessSecret,
								   charset));
	 }
	@SuppressWarnings("null")
	public AWSS3ClientAPI(final AWSS3ClientConfig config) {

		// Checks if client config is not null AmazonS3Client
		if (config == null) {
			Throwables.throwUnchecked(new IllegalArgumentException("In order to create instance of S3api, a client config, must be provided"));
		}
		_s3Client = _buildS3Client(config);
		_charset = config.getCharset();
		// build every sub-api
		_forBuckets = new AWSS3ClientAPIDelegateForBuckets(_s3Client);
		_forObjects  = new AWSS3ClientAPIDelegateForObjects (_s3Client);
		_forFiler = new AWSS3ClientAPIDelegateForFiler(_s3Client);
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ClientAPIDelegateForBuckets forBuckets() {
		return _forBuckets;
	}
	public AWSS3ClientAPIDelegateForObjects forObjects() {
		return _forObjects;
	}
	public AWSS3ClientAPIDelegateForFiler forFiler() {
		return _forFiler;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static S3Client _buildS3Client(final AWSS3ClientConfig config) {

		// Checks if client config is not null AmazonS3Client
		if (config == null) Throwables.throwUnchecked(new IllegalArgumentException("In order to create instance of S3api, a client config, must be provided"));

		// Minimun data for builder ( credentials & Amazon Enpoint )
		 //..but don't call to build before checking the rest of parameters.
		S3ClientBuilder s3ClientBuilder = S3Client.builder()
			                                   	  .region(config.getRegion())
		                                          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKey().asString(),
				                                                                                                                   config.getAccessSecret().asString())));
		//... Defaut S3 Configuration [see @S3Configuration] to work in all s3 based backends ( minio, ecs, amazon...)
		//    1. Path style to true [ no all s3 based systems has dns for buckets.
		//    2. Checksum validation  [ See https://stackoverflow.com/questions/59245962/min-io-with-aws-sdk-2-for-java-refuses-to-work-properly]
		S3Configuration s3Configuration = S3Configuration.builder()
				                                         .pathStyleAccessEnabled(true)
				                                         .checksumValidationEnabled(false)
				                                         .build();
		s3ClientBuilder.serviceConfiguration(s3Configuration);

		// ...next check endpoint (use the just created clientbuilder)
		if (config.getEndPoint() != null) {
			URI endPoint = null;
			try {
				endPoint = config.getEndPoint().asUrl().toURI();
			} catch (final MalformedURLException |  URISyntaxException e) {
				Throwables.throwUnchecked(new IllegalArgumentException(e.getLocalizedMessage()));
			}
			s3ClientBuilder.endpointOverride(endPoint);
		}
		// Dual Stack: Amazon S3 dual-stack endpoints support requests to S3 buckets over IPv6 and IPv4.
		// ...  windows minio must be false
		//  ... openshift wih scality must be true
		if (config.hasHttpSettings() &&
				config.getHttpSettings().isEnabledDualStack()) {
		  s3ClientBuilder.dualstackEnabled(true);
		 log.warn(" ...enabled dualstack");
		}

		// create custom http client and set it to s3 client.
		ApacheHttpClient.Builder httpClientBuilder = _createHttpClient(config);
		s3ClientBuilder.httpClientBuilder(httpClientBuilder);

		// return s3 client.
		S3Client outS3Client = s3ClientBuilder.build();
		

		return outS3Client;
	}
	private static ApacheHttpClient.Builder _createHttpClient(final AWSS3ClientConfig config) {
		// create a basic http client builder
		ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
																	 .connectionMaxIdleTime(Duration.ofHours(1))
																	 .socketTimeout(Duration.ZERO)
																	 .connectionTimeout(Duration.ZERO);


		// ... custom http settings
		if (config.getHttpSettings() != null) {
			// Set attributes, from GLOBAL_HTTP_DEFAULTS.
			software.amazon.awssdk.utils.AttributeMap.Builder attributeMapBuilder = SdkHttpConfigurationOption.GLOBAL_HTTP_DEFAULTS.toBuilder(); // See https://github.com/aws/aws-sdk-java-v2/blob/master/http-clients/apache-client/src/main/java/software/amazon/awssdk/http/apache/ApacheHttpClient.java
			// Disable SSLCertChecking
			if (config.getHttpSettings().isDisableCertChecking()) {
				log.warn(" ...Disabling cert checking");
				attributeMapBuilder.put(TRUST_ALL_CERTIFICATES, Boolean.TRUE);
				httpClientBuilder.tlsTrustManagersProvider(_trustAllTrustManager());
			}
			//httpClientBuilder.buildWithDefaults(attributeMapBuilder.build());

			// Check proxySettings.
			if (config.getHttpSettings().getProxySettings() != null
			 && config.getHttpSettings().getProxySettings().isEnabled()) {
				software.amazon.awssdk.http.apache.ProxyConfiguration.Builder proxyBuilder = ProxyConfiguration.builder()
																											   .useSystemPropertyValues(false)
																	                                           .endpoint(URI.create(Strings.customized("http://{}:{}",
																	                                        		                                   config.getHttpSettings().getProxySettings().getProxyHost(),
																	                		                                                           config.getHttpSettings().getProxySettings().getProxyPort())));
	            if (config.getHttpSettings().getProxySettings().getUser() != null ) {
	            	proxyBuilder.username(config.getHttpSettings().getProxySettings().getUser().asString());
	            }
	            if (config.getHttpSettings().getProxySettings().getPassword() != null ) {
	            	 proxyBuilder.password(config.getHttpSettings().getProxySettings().getPassword().asString());
	            }
	            ProxyConfiguration proxyConfig = proxyBuilder.build();
	            log.info(" \n \n Add proxy {}",proxyConfig);
	            httpClientBuilder.proxyConfiguration(proxyConfig);
			}
		}
		return httpClientBuilder;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	 private static TlsTrustManagersProvider _trustAllTrustManager() {
	     return  new TlsTrustManagersProvider() {
						@Override
						public TrustManager[] trustManagers() {
							return  new TrustManager[] {
										 new X509TrustManager() {
											  @Override
								                 public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
								                    // log.warn("Accepting a client certificate: " , x509Certificates[0].getSubjectDN());
								                 }
								                 @Override
								                 public void checkServerTrusted(final X509Certificate[]  x509Certificates, final String s) throws CertificateException {
								                     //log.warn("Accepting a client certificate: {}" ,x509Certificates[0].getSubjectDN());
								                 }
								                 @Override
								                 public X509Certificate[] getAcceptedIssuers() {
								                     return new X509Certificate[0];
								                 }
								          }
							};
						}};
     }

}
