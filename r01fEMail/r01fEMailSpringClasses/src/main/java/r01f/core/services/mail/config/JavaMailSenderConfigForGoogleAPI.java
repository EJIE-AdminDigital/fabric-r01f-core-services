package r01f.core.services.mail.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cloud.google.GoogleAPI.GoogleAPIClientEMailAddress;
import r01f.cloud.google.GoogleAPI.GoogleAPIClientID;
import r01f.cloud.google.GoogleAPI.GoogleAPIClientP12KeyPath;
import r01f.cloud.google.GoogleAPI.GoogleAPIServiceAccountClientData;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpClientProxySettings;
import r01f.httpclient.HttpProxySettingsBuilder;
import r01f.resources.ResourcesLoaderDef.ResourcesLoaderType;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class JavaMailSenderConfigForGoogleAPI
	 extends JavaMailSenderConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final GoogleAPIServiceAccountClientData _serviceAccountClientData;
	@Getter private final HttpClientProxySettings _proxySettings;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public JavaMailSenderConfigForGoogleAPI(final GoogleAPIServiceAccountClientData serviceAccountClientData,
											final HttpClientProxySettings proxySettigs,
											final boolean disabled) {
		super(JavaMailSenderImpl.GOOGLE_API,
			  disabled);
		_serviceAccountClientData = serviceAccountClientData;
		_proxySettings = proxySettigs;
	}
	public static JavaMailSenderConfigForGoogleAPI createFrom(final XMLPropertiesForAppComponent xmlProps,
												      		  final String propsRootNode) {
		// Get the google api info from the properties file
		GoogleAPIServiceAccountClientData serviceAccountClientData = JavaMailSenderConfigForGoogleAPI.googleAPIServiceAccountClientDataFromProperties(xmlProps,
																																				      propsRootNode);
		// check if a proxy is needed
		boolean disableMailSender = false;
		HttpClientProxySettings proxySettings = null;
		try {
			proxySettings = HttpProxySettingsBuilder.guessProxySettings(xmlProps,
																			  propsRootNode);
		} catch (Throwable th) {
			log.error("Error while guessing the internet connection proxy settings to use GMail: {}",th.getMessage(),th);
			disableMailSender = true;	// the mail sender cannot be used
		}

		// create the config object
		return new JavaMailSenderConfigForGoogleAPI(serviceAccountClientData,
													proxySettings,
													disableMailSender);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String GOOGLE_API_PROPS_XPATH = "/google/api";
	static GoogleAPIServiceAccountClientData googleAPIServiceAccountClientDataFromProperties(final XMLPropertiesForAppComponent props,
											 												 final String propsRootNode)	{
		String serviceAccountClientID = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/serviceAccountClientID")
											 .asString();
		String serviceAccountEMail = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/serviceAccountEmailAddress")
										  .asString();
		ResourcesLoaderType p12KeyLoader = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/p12Key/@loadedUsing")
								   				 .asEnumElementIgnoringCase(ResourcesLoaderType.class,
								   										    ResourcesLoaderType.FILESYSTEM);
		Path p12KeyFilePath = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/p12Key")
								   .asPath();
		EMail googleAppsUser = props.propertyAt(propsRootNode + GOOGLE_API_PROPS_XPATH + "/googleAppsUser")
									.asEMail();

		// Check
		if (serviceAccountClientID == null || serviceAccountEMail == null || p12KeyFilePath == null || googleAppsUser == null) {
			throw new IllegalStateException(Throwables.message("Cannot configure Google API: the properties file does NOT contains a the serviceAccountClientID, serviceAccountEMail, p12KeyFilePath or googleAppsUser at {} in {} properties file",
															   propsRootNode + GOOGLE_API_PROPS_XPATH,props.getAppCode()));
		}
		return new GoogleAPIServiceAccountClientData(props.getAppCode(),
												     GoogleAPIClientID.of(serviceAccountClientID),
												     GoogleAPIClientEMailAddress.of(serviceAccountEMail),
												     p12KeyLoader == ResourcesLoaderType.CLASSPATH ? GoogleAPIClientP12KeyPath.loadedFromClassPath(p12KeyFilePath)
														 									       : GoogleAPIClientP12KeyPath.loadedFromFileSystem(p12KeyFilePath),
												     googleAppsUser);
	}
}
