package r01f.brms.drools.model.output;

import java.io.Serializable;

public interface DroolsOutput<T> 
		 extends Serializable {
	public T getContent();
	public void setContent(T content);
}
