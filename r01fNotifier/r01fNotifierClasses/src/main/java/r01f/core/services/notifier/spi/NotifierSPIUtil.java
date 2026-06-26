package r01f.core.services.notifier.spi;

import java.util.Collection;
import java.util.ServiceLoader;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.core.services.notifier.config.NotifierConfigForPushMessage;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigForVoice;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;

import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Discovers all notifier impls and return the enabled impl
 * 
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			of every concrete implementation
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 * see:
 * 		- EMail: [r01fEMailSpringClasses]
 * 		- SMS: [r01fAWSSNSClasses] and [r01fLatiniaClasses]
 * 		- Voice: [r01fTwilioClasses]
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifierSPIUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static NotifierConfigForEMail emailNotifierConfigFrom(final XMLPropertiesForAppComponent props,
																 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering email notifiers");

		// Use java's SPI to get the available configs
		Collection<NotifierConfigForEMail> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForEMail.class)
					 .forEach(prov -> {
									log.info("\t...found email notifier provided by {}",
											 prov.getClass());
									try {
										cfgs.add(prov.provideEMailNotifierConfig(props,
																				 appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tEmail notifier provided by {} not accepted : {}",
												  prov.getClass(), ise.getMessage());
									}
							  });
		// Get the config for the selected service impl
		NotifierConfigForEMail selectedImplConfig = cfgs.stream()
														.filter(t -> t.isSelectedImpl())	// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked 
														.findFirst().orElse(null);
		if (selectedImplConfig == null) {
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForEMail.class));
		}
		log.info("\t > The selected email notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
	}
	
	
	

	public static NotifierConfigForEMail emailNotifierConfigFrom(final NotifierPropertiesContainer props,
																 final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering email notifiers");

		// Use java's SPI to get the available configs
		Collection<NotifierConfigForEMail> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForEMail.class)
					 .forEach(prov -> {
									log.info("\t...found email notifier provided by {}",
											 prov.getClass());
									try {
										cfgs.add(prov.provideEMailNotifierConfig(props,
																				 appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tEmail notifier provided by {} not accepted : {}",
												  prov.getClass(), ise.getMessage());
									}
							  });
		// Get the config for the selected service impl
		NotifierConfigForEMail selectedImplConfig = cfgs.stream()
														.filter(t -> t.isSelectedImpl())	// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked 
														.findFirst().orElse(null);
		if (selectedImplConfig == null) {
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForEMail.class));
		}
		log.warn("\t > The selected email notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
		
	}	
	
		
	
	public static NotifierConfigForSMS smsNotifierConfigFrom(final XMLPropertiesForAppComponent props,
															 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering sms notifiers");

		// Use java's SPI to get the available configs
		Collection<NotifierConfigForSMS> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForSMS.class)
					 .forEach(prov -> {
									log.info("\t...found sms notifier provided by {}",
											 prov.getClass());
									try {
										cfgs.add(prov.provideSMSNotifierConfig(props,
																			   appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tSMS notifier provided by {} not accepted : {}",
												 prov.getClass(), ise.getMessage());
									}
							  });
		// Get the config for the selected service impl
		NotifierConfigForSMS selectedImplConfig = cfgs.stream()
													  .filter(t -> t.isSelectedImpl())		// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked
													  .findFirst().orElse(null);
		if (selectedImplConfig == null) {
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForSMS.class));
		}
		log.info("\t > The selected sms notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
	}
	
	

	
	public static NotifierConfigForSMS smsNotifierConfigFrom(final NotifierPropertiesContainer props,
			                                                 final NotifierAppDependentConfigProviderFromRecordProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering sms notifiers");
		
		// Use java's SPI to get the available configs
		Collection<NotifierConfigForSMS> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForSMS.class)
						.forEach(prov -> {
									log.warn("\t...found sms notifier provided by {}",
									prov.getClass());
									try {
										cfgs.add(prov.provideSMSNotifierConfig(props,
																              appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tSMS notifier provided by {} not accepted : {}",
										prov.getClass(), ise.getMessage());
						}
						});
		// Get the config for the selected service impl
		NotifierConfigForSMS selectedImplConfig = cfgs.stream()
						  .filter(t -> t.isSelectedImpl())		// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked
						  .findFirst().orElse(null);
		if (selectedImplConfig == null) {
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForSMS.class));
		}
		log.warn("\t > The selected sms notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
	}

	
	
	
	public static NotifierConfigForVoice voiceNotifierConfigFrom(final XMLPropertiesForAppComponent props,
																 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering voice notifiers");

		// Use java's SPI to get the available configs
		Collection<NotifierConfigForVoice> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForVoice.class)
					 .forEach(prov -> {
									log.info("\t...found voice notifier provided by {}",
											 prov.getClass());
									try {
										cfgs.add(prov.provideVoiceNotifierConfig(props,
																		     	 appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tVoice notifier provided by {} not accepted : {}",
												 prov.getClass(), ise.getMessage());
									}
							  });
		// Get the config for the selected service impl
		NotifierConfigForVoice selectedImplConfig = cfgs.stream()
														.filter(t -> t.isSelectedImpl())	// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked
														.findFirst().orElse(null);
		if (selectedImplConfig == null) {
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForVoice.class));
		}
		log.info("\t > The selected voice notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
	}
	
	
	
	
	
	
	
	
	
	
	public static NotifierConfigForPushMessage pushMessageNotifierConfigFrom(final XMLPropertiesForAppComponent props,
																			 final NotifierAppDependentConfigProviderFromProperties appDepConfigProvider) {
		log.warn("[Notifier] discovering push message notifiers");

		// Use java's SPI to get the available configs
		Collection<NotifierConfigForPushMessage> cfgs = Lists.newArrayList();
		ServiceLoader.load(NotifierSPIProviderForPushMessage.class)
					 .forEach(prov -> {
									log.info("\t...found push message notifier provided by {}",
											 prov.getClass());
									try {
										cfgs.add(prov.providePushMessageNotifierConfig(props,
																					   appDepConfigProvider));
									} catch (IllegalStateException ise) {
										log.warn("\tPush notifier provided by {} not accepted : {}",
												 prov.getClass(),ise.getMessage());
									}
							  });
		// Get the config for the selected service impl
		NotifierConfigForPushMessage selectedImplConfig = cfgs.stream()
															  .filter(t -> t.isSelectedImpl())	// do NOT convert to method ref > weird error in jdk 17: MethodHandle(NotifierConfigForSMS)boolean is not direct or cannot be cracked
															  .findFirst().orElse(null);
		if (selectedImplConfig == null) {
			// if no impl was found check that the project contains a type extending NotifierSPIProviderForPushMessage
			// (maybe there's NO dep containing a type extending NotifierSPIProviderForPushMessage)
			throw new IllegalStateException(_composeNOSPIProviderFoundErrorMsg(NotifierSPIProviderForPushMessage.class));
		}
		log.info("\t > The selected push message notifier impl is {}",selectedImplConfig.getImpl());
		return selectedImplConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _composeNOSPIProviderFoundErrorMsg(final Class<?> spiProviderType) {
		return Strings.customized("Could NOT find SPI provider for {}: CHECK previous error messages and ensure there exists a class extending {} " +
								  "and that the project containig that class MUST contain a file at META-INF/services called r01f.core.services.notifier.spi.{} that 'points' to the {} implementation",
								  spiProviderType.getSimpleName(),
								  spiProviderType,
								  spiProviderType.getSimpleName(),
								  spiProviderType.getSimpleName());
	}
}
