package r01f.core.notifier;

import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.NotifierServiceResponse;


@Accessors(prefix="_")
@RequiredArgsConstructor
public class NotifierResponseResultImplMultiple<T> 
  	 extends NotifierResponseResultBase<T>
  implements NotifierResponseResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
  @Getter private final Collection<NotifierServiceResponse<T>> _serviceResponse;
}
