package r01f.core.services.notifier.properties;

public record NotifiersStrategy(NotifierPropertiesForMail email,
		                        NotifierPropertiesForSMS sms,
		                        NotifierPropertiesForPush push) {}