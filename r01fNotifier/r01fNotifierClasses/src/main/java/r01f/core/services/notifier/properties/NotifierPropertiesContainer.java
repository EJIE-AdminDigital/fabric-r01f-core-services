package r01f.core.services.notifier.properties;

import r01f.guids.CommonOIDs.AppCode;

public record NotifierPropertiesContainer(
		                                  AppCode appcode, 
		                                  /*[1] - A 'common' part: contains common notifier properties like whether
		                                  *		  the notifier is enabled or the [from] address
		                                  *		   This properties DO NOT depend on the specific
		                                  *		   implementation of the notifier*/
		                                  NotifiersStrategy notifiers, 
		                                  
		                                  /*[1] - A 'impl' part: contains custom notifier imps like 
			                                  *		   This properties depend on the specific
			                                  *		   implementation of the notifier*/
		                                  NotifierImpPropertiesForMail impForMail , 		
		                                  NotifierImpPropertiesForSMS  implForSMS
										  ) {
	

}
