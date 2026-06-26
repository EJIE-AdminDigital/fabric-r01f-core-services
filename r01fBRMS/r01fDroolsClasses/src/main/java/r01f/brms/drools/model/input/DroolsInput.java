package r01f.brms.drools.model.input;

import java.io.Serializable;

public interface DroolsInput<T> 
		 extends Serializable {
	public T getContent();
	public void setContent(T content);
}
