package r01f.core.services.notifier;

import java.util.Collection;

import r01f.patterns.Factory;

public interface NotifierService<F,T,	// FROM & TO
								 M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract NotifierServiceResponse<T> notify(final F from, final T to,
							   				          final Factory<M> messageToBeDeliveredFactory);

	public abstract Collection<NotifierServiceResponse<T>> notifyAll(final F from,final Collection<T> to,
											  				         final Factory<M> messageToBeDeliveredFactory);

}
