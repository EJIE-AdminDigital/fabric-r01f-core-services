package r01f.core.services.mail;

import java.util.Properties;

import r01f.cloud.aws.AWSAccessKey;
import r01f.cloud.aws.AWSAccessSecret;
import r01f.core.services.notifier.properties.NotifierImplDependentProperties.NotifierImplRecordProperties;
import r01f.marshalling.properties.PropertiesToRecordMarshaller;
import r01f.marshalling.properties.PropertiesToRecordMarshallerBuilder;
import r01f.types.url.Host;
import r01f.types.url.Url;

public record JavaMailSenderProperties( SMTPProperties smtp,
										AWSProperties aws,
										RESTProperties rest) {
/////////////////////////////////////////////////////////////////////////////////////////
//  Marshaller for utility
/////////////////////////////////////////////////////////////////////////////////////////       
	private static final PropertiesToRecordMarshaller _marshaller = PropertiesToRecordMarshallerBuilder.create();
/////////////////////////////////////////////////////////////////////////////////////////
// RECORDS
/////////////////////////////////////////////////////////////////////////////////////////                                                        
    public record SMTPProperties (Host host, int port) implements  NotifierImplRecordProperties {
		public static SMTPProperties from(final Properties data) {				
			return _marshaller.forType(SMTPProperties.class).propertiesToRecord(data);				
		}}
    
    public record AWSProperties  (AWSAccessKey accessKey, AWSAccessSecret accessSecret) implements NotifierImplRecordProperties{	    	
    	public static AWSProperties from(final Properties data) {	
    		return _marshaller.forType(AWSProperties.class).propertiesToRecord(data);
	
		}}
    
    public record RESTProperties (Url endpoint) implements  NotifierImplRecordProperties {
    	public static RESTProperties from(final Properties data) {			
    		return _marshaller.forType(RESTProperties.class).propertiesToRecord(data);
		}
    }
}