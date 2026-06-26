package r01f.brms.drools.model.output;

import lombok.NoArgsConstructor;
import r01f.locale.I18NKey;

@NoArgsConstructor
public class DroolsI18NTextOutput
	 extends DroolsOutputBase<I18NKey> {

	private static final long serialVersionUID = 2830007256852149722L;

	public DroolsI18NTextOutput(final I18NKey content) {
		super(content);
	}

	public static DroolsI18NTextOutput forKey(String i18nKey) {
		return new DroolsI18NTextOutput(I18NKey.forId(i18nKey));
	}

	@Override
	public I18NKey getContent() {
		return _content;
	}

	@Override
	public void setContent(I18NKey content) {
		_content = content;
	}
}
