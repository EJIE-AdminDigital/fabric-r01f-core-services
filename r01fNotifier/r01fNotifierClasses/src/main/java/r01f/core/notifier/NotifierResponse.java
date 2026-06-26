package r01f.core.notifier;

/**
 * Just an interface to return a response about the nofifier:
 *
 *  1. Just to know if notification was successful.
 *  2. Get the underlying services response (sms latinia,  push-firebase...)
 *  
 *  If there is some custom or extended implementation of Notifier just return:
 *  <pre class='brush:java'>
 *  		   return new  NotifierResponse() {
 *									@Override
 *									public boolean wasSuccessful() {									
 *										return serviceResponse.wasSuccessful(); //<< reponse based on underlaying service response.
 *									}						
 *								
 *									@Override 
 *									public NotifierResponseResult<Phone> getServiceResponse() {										
 *										 1. If NotifierResponseResult is single
 *										      return  serviceResponse;
 *										 2. If NotifierResponseResult  is multiple:
 *									}} ;
 *	    }
 * </pre>
 */
public interface NotifierResponse {
	
	public boolean wasSuccessful();	
	
	public <T> NotifierResponseResult<T> getResponseResult();	
}
