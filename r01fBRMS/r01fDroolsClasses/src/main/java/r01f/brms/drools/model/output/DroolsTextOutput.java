package r01f.brms.drools.model.output;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DroolsTextOutput 
	 extends DroolsOutputBase<String> {

	private static final long serialVersionUID = 8715928278580215236L;

	public DroolsTextOutput(String content) {
		super(content);
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
