package r01f.cloud.aws;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.httpclient.HttpClientProxySettings;


@Accessors(prefix="_")
public class AWSClientHttpSettings
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter  private final  Boolean  _disableCertChecking ;
	@Getter  private final  Boolean  _enableDualStack ;      //Amazon S3 dual-stack endpoints support requests to S3 buckets over IPv6 and IPv4.
    @Getter  private final  HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public AWSClientHttpSettings() {
    	this(false,false,null);
	}
	public AWSClientHttpSettings(final boolean disableCertChecking,
			                     final boolean enableDualStack,
						         final HttpClientProxySettings proxySettings) {
		_disableCertChecking = disableCertChecking;
		_proxySettings  = proxySettings;
		_enableDualStack = enableDualStack;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FROM PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isDisableCertChecking() {
		return _disableCertChecking != null
						&& _disableCertChecking == true ;
	}
	public boolean isEnabledDualStack() {
		return _enableDualStack != null
						&& _enableDualStack == true ;
	}
}
