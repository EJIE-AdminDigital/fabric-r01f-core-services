package r01f.core.services.translator;

import r01f.locale.Language;
import r01f.types.url.Url;
import r01f.types.url.web.WebLink;

/////////////////////////////////////////////////////////////////////////////////////////
//	TRANSLATE
/////////////////////////////////////////////////////////////////////////////////////////
	public interface TextTranslatorService {
		/**
		 * Translates a text (no html markup)
		 * @param text
		 * @param fromLang
		 * @param toLang
		 * @return
		 */
		public String translateText(final String text,final Language fromLang,final Language toLang);
		/**
		 * Translates HTML text
		 * @param text
		 * @param fromLang
		 * @param toLang
		 * @return
		 */
		public String translateHTML(final String text,final Language fromLang,final Language toLang);
/////////////////////////////////////////////////////////////////////////////////////////
//	LANG REFERENCES
/////////////////////////////////////////////////////////////////////////////////////////		
		/**
		 * Changes all lang references in ISO639_1 code (es, eu, en, fr...)
		 * It searches for iso639_1 references like /es/, -es-, _es_, $es$, and so on
		 * @param text
		 * @param fromLang
		 * @param toLang
		 * @return
		 */
		public String replaceReferencesOfLangISO639_1(final String text,final Language fromLang,final Language toLang);
/////////////////////////////////////////////////////////////////////////////////////////
//	URL
/////////////////////////////////////////////////////////////////////////////////////////		
		/**
		 * Translates an URL
		 * @param url
		 * @param fromLang
		 * @param toLang
		 * @return
		 */
		public Url translateUrl(final Url url,final Language fromLang,final Language toLang);
		/**
		 * Translates a WebLink
		 * @param link
		 * @param fromLang
		 * @param toLang
		 * @return
		 */
		public WebLink translateWebLink(final WebLink link,final Language fromLang,final Language toLang);
	}