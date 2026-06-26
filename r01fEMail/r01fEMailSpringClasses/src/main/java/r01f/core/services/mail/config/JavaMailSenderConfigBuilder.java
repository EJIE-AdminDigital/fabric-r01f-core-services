package r01f.core.services.mail.config;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.mail.notifier.JavaMailSenderNotifierProperties;
import r01f.exceptions.Throwables;
import r01f.patterns.IsBuilder;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Usage:
 * <pre class='brush:java>
 * 		JavaMailSenderConfigForAWSSES awsSESMailSenderCfg = JavaMailSenderConfigBuilder.of(JavaMailSenderImpl.AWS)
 * 																					   .from(props);
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JavaMailSenderConfigBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static JavaMailSenderConfigBuilderXMLPropertiesStep of(final JavaMailSenderImpl impl) {
		return new JavaMailSenderConfigBuilder() { /* nothing */ }
					.new JavaMailSenderConfigBuilderXMLPropertiesStep(impl);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JavaMailSenderConfigBuilderXMLPropertiesStep {
		private final JavaMailSenderImpl _impl;

		public JavaMailSenderConfigBase from(final XMLPropertiesForAppComponent xmlProps) {
			return this.from(xmlProps,
						     "mail");
		}
		@SuppressWarnings("unchecked")
		public <C extends JavaMailSenderConfigBase> C from(final XMLPropertiesForAppComponent xmlProps,
													       final String propsRootNode) {
			String thePropsRootNode = Strings.isNullOrEmpty(propsRootNode) ? "mail" : propsRootNode;
			C outConfig = null;

			// java mail sender impl
			log.debug("{} impl={} loaded from {}.{} properties (root node={})",
					   JavaMailSenderConfig.class.getSimpleName(),_impl,
					   xmlProps.getAppCode(),xmlProps.getAppComponent(),propsRootNode);

			// ==== SMTP
			if (_impl == JavaMailSenderImpl.SMTP) {
				JavaMailSenderConfigForSMTP smtpCfg = JavaMailSenderConfigForSMTP.createFrom(xmlProps,
																							 thePropsRootNode);
				outConfig = (C)smtpCfg;
			}

			// ==== AWS SES (simple email service)
			else if (_impl == JavaMailSenderImpl.AWS_SES) {
				JavaMailSenderConfigForAWSSES awsSESCfg = JavaMailSenderConfigForAWSSES.createFrom(xmlProps,
																							 	   thePropsRootNode);
				outConfig = (C)awsSESCfg;
			}

			// ==== GOOGLE GMAIL API
			else if (_impl == JavaMailSenderImpl.GOOGLE_API) {
				JavaMailSenderConfigForGoogleAPI gApiCfg = JavaMailSenderConfigForGoogleAPI.createFrom(xmlProps,
																									   thePropsRootNode);
				outConfig = (C)gApiCfg;
			}

			// ==== GOOGLE GMAIL SMTP
			else if (_impl == JavaMailSenderImpl.GOOGLE_SMTP) {
				JavaMailSenderConfigForGoogleSMTP gSMTPCfg = JavaMailSenderConfigForGoogleSMTP.createFrom(xmlProps,
																										  thePropsRootNode);
				outConfig = (C)gSMTPCfg;

			// ==== REST SERVICE
			} else if (_impl == JavaMailSenderImpl.REST_SERVICE ) {
				JavaMailSenderConfigForRESTService thirdPartyHttpCfg = JavaMailSenderConfigForRESTService.createFrom(xmlProps,
																													 thePropsRootNode);
				outConfig = (C)thirdPartyHttpCfg;

			} else {
				throw new IllegalStateException(Throwables.message("Spring javaMailSender impl={} config was NOT configured at {}/{} in {} properties file",
																   _impl,
																   thePropsRootNode,_impl,
																   xmlProps.getAppCode(),xmlProps.getAppComponent()));
			}
			return outConfig;
		}
		
		@SuppressWarnings("unchecked")
		public <C extends JavaMailSenderConfigBase> C from(final JavaMailSenderNotifierProperties props) {		
			C outConfig = null;
			// java mail sender impl
			log.warn("{} impl={} loaded from {}.{} properties (root node={})",
					   JavaMailSenderConfig.class.getSimpleName(),_impl);
			// ==== SMTP
			if (_impl == JavaMailSenderImpl.SMTP) {
				JavaMailSenderConfigForSMTP smtpCfg = JavaMailSenderConfigForSMTP.createFrom(props.smtp());
				outConfig = (C)smtpCfg;
			}

			// ==== AWS SES (simple email service)
			else if (_impl == JavaMailSenderImpl.AWS_SES) {
				JavaMailSenderConfigForAWSSES awsSESCfg = JavaMailSenderConfigForAWSSES.createFrom(props.aws());
				outConfig = (C)awsSESCfg;
			} else if (_impl == JavaMailSenderImpl.REST_SERVICE ) {
				JavaMailSenderConfigForRESTService thirdPartyHttpCfg = JavaMailSenderConfigForRESTService.createFrom(props.rest());
				outConfig = (C)thirdPartyHttpCfg;

			} else {
				throw new IllegalStateException(Throwables.message("Spring javaMailSender impl={} config was NOT configured at @Spring Properties",
																   _impl));
			}
			return outConfig;
		}		
	}
	

}
