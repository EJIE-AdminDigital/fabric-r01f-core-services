package r01f.cloud.firebase.service;

import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.firebase.service.FirebaseServiceImpl.FirebaseAPIData;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.Environment;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpProxySettingsBuilder;
import r01f.io.util.StreamEncrypterDecrypter;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesEnv;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class FirebaseConfig
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final FirebaseAPIData _apiData;
	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FirebaseConfig(final FirebaseAPIData apiData,
						  final HttpClientProxySettings proxySettings) {
		_proxySettings = proxySettings;
		_apiData = apiData;
	}
	public static FirebaseConfig createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return createFrom(xmlProps,
						 "firebase");
	}
	public  static FirebaseConfig createFrom(final XMLPropertiesForAppComponent xmlProps,
										     final String propsRootNode) {
		// ensure the root node
		String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "firebase" : propsRootNode;

		// Test proxy connection to see if proxy is needed
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpProxySettingsBuilder.guessProxySettings(xmlProps,
																			  thePropsRootNode);
		} catch (final Throwable th) {
			log.error("Error while guessing the proxy settings to use Twilio: {}",
					  th.getMessage(),th);
		}

		// Get the google credentials api info from the properties file
		FirebaseAPIData apiData = FirebaseConfig.apiDataFromProperties(xmlProps,
																       thePropsRootNode);

		// return the config
		return new FirebaseConfig(apiData,
								  proxySettings);
	}
	
	
	public  static FirebaseConfig createFrom(final Path credentialsPath, final SecurityToken secToken) {
		// Get the google credentials api info from the properties file
		FirebaseAPIData apiData = FirebaseConfig.apiDataFromPath(credentialsPath,
				                                                  secToken);
		// return the config
		return new FirebaseConfig(apiData,
								  null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	
	 static FirebaseAPIData apiDataFromProperties(final XMLPropertiesForAppComponent props,
		      									  final String propsRootNode) {
		 try {
			String credentialsPath = props.propertyAt(propsRootNode + "/credentials")
				 	  .asString();
			if (Strings.isNullOrEmpty(credentialsPath)) throw new IllegalStateException("Could NOT found firebase credentials file path at " + credentialsPath);
			log.warn("Loading Google Firebase Credentials from {}",
			credentialsPath);
			
			String keyAsString = props.propertyAt(propsRootNode + "/credentials/@key")
													.asString();
			return apiDataFromPath(Path.from(credentialsPath), SecurityToken.from(keyAsString));
				 
		 } catch (final Throwable e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getLocalizedMessage());
		}		 
			
		 
	 }
	 static FirebaseAPIData apiDataFromPath(final Path credentialsPath, final SecurityToken secKey) {
	
		GoogleCredentials googleCredentials;
		try {
			// Load a json file from the classpath that contains the firebase key
			// BEWARE!!!!
			// If the credentials file is a JSON file, include the following at the [config] project pom.xml
			// to enable 
			//    <resources>
			//    	<resource>
			//    		<directory>src/main/config</directory>
			//    		<includes>
			//    			<include>**/*.xml</include>
			//    			<include>**/*.json</include>
			//    		</includes>
			//    	</resource>
			//    	<resource>
			//    		<directory>src/main/resources</directory>
			//    	</resource>
			//    </resources>
			ResourcesLoader resLoader = ResourcesLoaderBuilder.createDefaultResourcesLoader();
			InputStream stream = resLoader.getInputStream(Path.from(credentialsPath));
			
			if (stream == null) {
				Environment env = XMLPropertiesEnv.guessEnvironmentFromSystemEnvProp();
				Path credentialPathByEnv = Path.from(env.getId(),credentialsPath);
				log.warn("... could NOT find firebase credentials file at {}: try to find it at the env-dependent path {}",
						 credentialsPath,credentialPathByEnv.asRelativeString());
				
				stream = resLoader.getInputStream(Path.from(credentialPathByEnv));
				
				if (stream == null) throw new IllegalStateException(Strings.customized("Could NOT find firebase credentials at {}, neither at {}",
																				   	   credentialsPath,credentialPathByEnv)) ;
			}			
			// get the key and create the api
			String keyAsString = secKey == null? null : secKey.asString();
			if (keyAsString == null) {			
				googleCredentials = GoogleCredentials.fromStream(stream);			
			} else {				
				StreamEncrypterDecrypter encdec = new StreamEncrypterDecrypter(keyAsString.trim().toCharArray());
				googleCredentials = GoogleCredentials.fromStream(encdec.decrypt(stream));
			}			
			// Create the firebase service
			FirebaseAPIData apiData = new FirebaseAPIData(googleCredentials);
			return apiData;
		} catch (final IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}
}
