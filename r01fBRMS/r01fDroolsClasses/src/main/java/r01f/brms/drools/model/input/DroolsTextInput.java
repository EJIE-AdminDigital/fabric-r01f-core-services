package r01f.brms.drools.model.input;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DroolsTextInput 
	extends DroolsInputBase<String> {

	private static final long serialVersionUID = 3541868259856287680L;

	public DroolsTextInput(String text) {
		super(text);
	}
	
	public static DroolsTextInput from(String text) {
		return new DroolsTextInput(text);
	}

	@Override
	public String getContent() {
		return _content;
	}

	@Override
	public void setContent(String content) {
		_content = content;
	}
}
