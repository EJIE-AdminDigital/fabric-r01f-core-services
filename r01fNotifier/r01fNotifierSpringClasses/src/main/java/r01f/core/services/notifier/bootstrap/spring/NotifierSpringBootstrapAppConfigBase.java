package r01f.core.services.notifier.bootstrap.spring;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.NotifierServiceForEMail;
import r01f.core.services.notifier.NotifierServiceForSMS;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromRecordProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.properties.NotifierImpPropertiesForMail;
import r01f.core.services.notifier.properties.NotifierImpPropertiesForSMS;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.MailSenderNotifierImplProperties;
import r01f.core.services.notifier.properties.NotifierImplPropertiesBase.SMSSenderNotifierImplProperties;
import r01f.core.services.notifier.properties.NotifierPropertiesContainer;

import r01f.core.services.notifier.spi.NotifierSPIProviderForEMail;
import r01f.core.services.notifier.spi.NotifierSPIProviderForSMS;
import r01f.core.services.notifier.spi.NotifierSPIUtil;

@Slf4j

public abstract class NotifierSpringBootstrapAppConfigBase {
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
///  BASE PROPS & CONTAINER
////////////////////////////////////////////////////////////////////////////////////////////////////
	@Bean @SuppressWarnings("static-method")
    public NotifierImpPropertiesForMail notifierImpPropertiesForMail(final Environment environment) {
	    // 1. Obtenemos el Binder a partir del Environment de Spring
        Binder binder = Binder.get(environment);
        
        // 2. Realizamos el binding manual del prefijo "notifier" al Record
        // Esto instanciará los records anidados (NotifiersStrategy, etc.) correctamente.
        MailSenderNotifierImplProperties implsForMail
           = binder.bind("notifier.impl-for-mail", MailSenderNotifierImplProperties.class)
                     .get(); // Si no existe en el YAML lanzará una excepción
       
        return implsForMail;
	}
	
	
	
	
	@Bean @SuppressWarnings("static-method")
    public NotifierImpPropertiesForSMS notifierImpPropertiesForSMS(final Environment environment) {
	    // 1. Obtenemos el Binder a partir del Environment de Spring
        Binder binder = Binder.get(environment);
        
        // 2. Realizamos el binding manual del prefijo "notifier" al Record
        // Esto instanciará los records anidados (NotifiersStrategy, etc.) correctamente.
        SMSSenderNotifierImplProperties implsForMail
           = binder.bind("notifier.impl-for-sms", SMSSenderNotifierImplProperties.class)
                     .get(); // Si no existe en el YAML lanzará una excepción
       
        return implsForMail;
	}
	
	
	@Bean @SuppressWarnings("static-method")
    public NotifierPropertiesContainer notifierPropertiesContainer(final Environment environment, 
    		                                                       final NotifierImpPropertiesForMail mailImpl,
    		                                                       final NotifierImpPropertiesForSMS smsImpl) {
        // 1. Obtenemos el Binder a partir del Environment de Spring
        Binder binder = Binder.get(environment);
        
        // 2. Realizamos el binding manual del prefijo "notifier" al Record
        // Esto instanciará los records anidados (NotifiersStrategy, etc.) correctamente.
        NotifierPropertiesContainer pre = binder.bind("notifier", NotifierPropertiesContainer.class)
                     							.get(); // Si no existe en el YAML lanzará una excepción
        return new NotifierPropertiesContainer(
								        		pre.appcode(),
								        		pre.notifiers(),
								                mailImpl, 
								                smsImpl
            );        
    }	
/////////////////////////////////////////////////////////////////////////////////////////////////////
///  EMAL
////////////////////////////////////////////////////////////////////////////////////////////////////

	@Bean 	@SuppressWarnings("static-method")
	NotifierConfigForEMail  notifierServiceforEMailConfig  (final NotifierPropertiesContainer container) {		
		NotifierAppDependentConfigProviderFromRecordProperties appProps  = new NotifierAppDependentConfigProviderFromRecordProperties() {
			@Override
			public ContainsConfigData provideConfigUsing(NotifierImpl impl, NotifierPropertiesContainer props) {			
				return null;
			}			
		};

		NotifierConfigForEMail forEMail = NotifierSPIUtil.emailNotifierConfigFrom( container,
				                                                                   appProps);
		return forEMail;
	}
		

	@Bean 	@SuppressWarnings("static-method")
	NotifierServiceForEMail  notifierServiceforEmail  (final NotifierConfigForEMail forEMail) {		
		log.info("[Notifier]: SPI finding {} implementations",
				  NotifierSPIProviderForEMail.class);
		// BEWARE! there MUST exists a file named as the spi provider interface FQN at the META-INF folder
		//		   of every implementation project
		NotifierServiceForEMail outSrvc = null;
		for (Iterator<NotifierSPIProviderForEMail> pIt = ServiceLoader.load(NotifierSPIProviderForEMail.class).iterator(); pIt.hasNext(); ) {
			NotifierSPIProviderForEMail prov = pIt.next();

			outSrvc = prov.provideEMailNotifier(forEMail);
		}
		if (outSrvc == null) throw new IllegalStateException("Could NOT find any email notifier implementation!");
		return outSrvc;		
	}	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/// SMS
////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	@Bean 	@SuppressWarnings("static-method")
	NotifierConfigForSMS  notifierServiceforSMSConfig  (final NotifierPropertiesContainer container) {		
		NotifierAppDependentConfigProviderFromRecordProperties appProps  = new NotifierAppDependentConfigProviderFromRecordProperties() {
			@Override
			public ContainsConfigData provideConfigUsing(NotifierImpl impl, NotifierPropertiesContainer props) {			
				return null;
			}			
		};
		NotifierConfigForSMS forSMS = NotifierSPIUtil.smsNotifierConfigFrom( container,
				                                                             appProps);
		return forSMS;
	}
	
	

	@Bean 	@SuppressWarnings("static-method")
	NotifierServiceForSMS  notifierServiceforSMS  (final NotifierConfigForSMS forSMS) {		
		NotifierServiceForSMS outSrvc = null;
		for (Iterator<NotifierSPIProviderForSMS> pIt = ServiceLoader.load(NotifierSPIProviderForSMS.class).iterator(); pIt.hasNext(); ) {
			NotifierSPIProviderForSMS prov = pIt.next();

			if (forSMS.getImpl().is(prov.getImpl())) {
				log.info("\t...found impl={} (ENABLED)",
						 prov.getImpl());
				outSrvc = prov.provideSMSNotifier(forSMS);
			} else {
				log.info("\t...found impl={} (NOT ENABLED)",
						 prov.getImpl());
			}
		}
		if (outSrvc == null) throw new IllegalStateException("Could NOT find any SMS notifier implementation!");
		return outSrvc;
	}
		
}