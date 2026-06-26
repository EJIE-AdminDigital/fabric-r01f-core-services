package r01f.brms.drools.model.output;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class DroolsOutputBase<T>
  		   implements DroolsOutput<T> {

	private static final long serialVersionUID = 6798428250393205147L;
	
	protected T _content;
	
	public DroolsOutputBase(T content) {
		_content = content;
	}
	
	@Override
	public abstract T getContent();
	@Override
	public abstract void setContent(T content);
}
