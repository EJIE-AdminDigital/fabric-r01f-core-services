package r01f.brms.drools.model.input;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class DroolsInputBase<T>
  		   implements DroolsInput<T> {

	private static final long serialVersionUID = 6898220153296202765L;

	protected T _content;

	public DroolsInputBase(final T content) {
		_content = content;
	}

	@Override
	public abstract T getContent();
	@Override
	public abstract void setContent(T content);
}
