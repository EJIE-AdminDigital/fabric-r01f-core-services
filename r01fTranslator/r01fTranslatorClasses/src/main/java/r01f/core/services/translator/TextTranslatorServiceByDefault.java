package r01f.core.services.translator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import r01f.locale.Language;
import r01f.types.url.Url;
import r01f.types.url.web.WebLink;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

public class TextTranslatorServiceByDefault 
  implements TextTranslatorService {
/////////////////////////////////////////////////////////////////////////////////////////
//	TEXT & HTML TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String translateText(final String text,final Language fromLang,final Language toLang) {
		return text != null && !text.isEmpty() ? Strings.customized("[{}] {}",
								  								   toLang.getIso639_1(),text) 
											   : "";
	}
	@Override
	public String translateHTML(final String text,final Language fromLang,final Language toLang) {
		// Add tags indicating target lang, return original text until "Itzuli" functionality is implemented
		return text != null && !text.isEmpty() ? Strings.customized("<p>[{}]</p> {}",
								  								   toLang.getIso639_1(),text) 
											   : "";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	URL & WEBLINK TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public WebLink translateWebLink(final WebLink link,final Language fromLang,final Language toLang) {
		WebLink outLink = new WebLink();
		// lang
		outLink.setLanguage(toLang);
		
		// text, title, tags...
		if (link.getText() != null) 		outLink.setText(this.translateText(link.getText(),fromLang,toLang));
		if (link.getDescription() != null) 	outLink.setDescription(this.translateText(link.getDescription(),fromLang,toLang));
		if (link.getTitle() != null)		outLink.setTitle(this.translateText(link.getTitle(),fromLang,toLang));
		if (CollectionUtils.hasData(link.getTags())) outLink.setTags(link.getTags()
																		 .stream()
																		 .filter(Objects::nonNull)
																		 .map(aTag -> this.translateText(aTag,fromLang,toLang))
																		 .collect(Collectors.toList()));
		// presentation
		outLink.setPresentation(link.getPresentation());
		
		// link
		outLink.setUrl(this.translateUrl(link.getUrl(),fromLang,toLang));
		
		return outLink; 
	}
	@Override
	public Url translateUrl(final Url url, final Language fromLang, final Language toLang) {
		String urlStr = url != null ? url.asString() : null;
		String translatedUrl = this.replaceReferencesOfLangISO639_1(urlStr,fromLang,toLang);
		return translatedUrl != null ? Url.from(translatedUrl) : null;
	}
	@Override
	public String replaceReferencesOfLangISO639_1(final String text,final Language fromLang,final Language toLang) {
		if (Strings.isNullOrEmpty(text)) return null;
		
		StringBuilder out = new StringBuilder();
		Matcher matcher = _createLangISO639_1_InUrlPatternFor(fromLang).matcher(text);
		
		int lastIndex = 0;
		while (matcher.find()) {
			out.append(text,lastIndex,matcher.start())		// append a subsequence of [text] from last index to matcher.start()
			   .append(toLang.getIso639_1());				// replace
			lastIndex = matcher.end();						// start over from the last matching point
			if (lastIndex < text.length()) {
				out.append(text.charAt(lastIndex - 1)); // append next character of matched string, if it is not located at the end
			} else if (lastIndex == text.length() && "/".equals(text.substring(lastIndex-1))) {
				out.append("/"); // ensure slash character is preserved if it is located at the end
			}
		}
		if (lastIndex < text.length()) out.append(text,lastIndex,text.length());	// the remainder
		return out.toString();
	}
	private static Pattern _createLangISO639_1_InUrlPatternFor(final Language replaced) {
		// Match the lang iso639_1 code in an url like:
		// 		http://my-site.en/en/foo-en-bar/thisenshouldnotmatch/enneithershouldthis/en
		//                      *  *      *          X                X                   *
		return Pattern.compile(Strings.customized("(?<=[\\.\\/\\-_=\\$])({})(?:[\\.\\/\\-_\\$])" + // Matching within text. Special characters should flank the ISO639_1 lang string
												  "|(?<=[\\.\\/\\-_=\\$])({})$",					  // Matching at the end of text. A special character should precede the ISO639_1 lang string
												  replaced.getIso639_1(), replaced.getIso639_1()));
	}
}
