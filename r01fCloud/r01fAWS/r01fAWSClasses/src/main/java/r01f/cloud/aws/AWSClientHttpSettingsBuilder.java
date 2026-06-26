package r01f.cloud.aws;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.httpclient.HttpClientProxySettings;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.url.Host;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@NoArgsConstructor(access=AccessLevel.PACKAGE)
@Slf4j
public abstract class AWSClientHttpSettingsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AWSClientHttpSettings fromXMLProperties(final XMLPropertiesForAppComponent props,
														        final String propsRootNode,final AWSService awsService) {
		// No HttpSettings.
		if ( ! props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings").exist() ) {
			return null;
		}

		boolean disableCertChecking = props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/@disableCertChecking")
													.asBoolean(false);

		boolean enableDualStack     =    props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/@enableDualStack")
													.asBoolean(false);

		// Return with no ProxySettings...
		if (! props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings").exist() ) {
			return new AWSClientHttpSettings(disableCertChecking,enableDualStack,null);
		}
		 //////////  ProxySettings
		boolean enabled = props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings/@enabled/").asBoolean(false);
		int port =        props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings/port").asInteger(80);

	    Host host =       props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings/host")
											.asObjectFromString(new FactoryFrom<String,Host>() {
												@Override
												public Host from(final String value) {
													return Host.from(value);
												}
										  },
								      null);
		 LoginID loginId =    props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings/user")
											.asObjectFromString(new FactoryFrom<String,LoginID>() {
																		@Override
																		public  LoginID from(final String value) {
																			return LoginID.forId(value);
																		}
																 },
								      null);
		 Password password =   props.propertyAt(propsRootNode + "/aws/" + AWSService.S3.nameLowerCase() + "/httpSettings/proxySettings/password")
										.asObjectFromString(new FactoryFrom<String,Password>() {
											@Override
											public  Password from(final String value) {
												return Password.forId(value);
											}
									  },
							      null);

		 HttpClientProxySettings proxye = new HttpClientProxySettings(host,port, loginId, password,enabled);
		 log.warn( "Proxy Info {}",proxye.debugInfo());
		// Return with  ProxySettings...
		 return new AWSClientHttpSettings(disableCertChecking,enableDualStack,proxye);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

}
