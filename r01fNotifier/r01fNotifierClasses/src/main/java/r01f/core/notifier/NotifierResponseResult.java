package r01f.core.notifier;

/**
 * The underliying servives respose can be simple ( NotifierServiceResponse<T> ) 
 *  ..... or multiple  ( Collection<NotifierServiceResponse<T>)
 */
public interface NotifierResponseResult<T> {	
	NotifierResponseResultImpl<T> asSingle();
	NotifierResponseResultImplMultiple<T> asMultiple();
}
