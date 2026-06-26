package r01f.core.services.translator;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.types.url.Url;
import r01f.types.url.web.WebLink;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.StringEscapeUtils;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class TextTranslatorServiceByExternalAPI
  implements TextTranslatorService {
	// TODO get values from external properties file
	// lang code placeholder constants
	final String FROM_LANG_CODE_PLACEHOLDER = "[from]";
	final String TO_LANG_CODE_PLACEHOLDER = "[to]";
	// API call throttling params
	final int MAX_JSON_CHUNK_SIZE = 140;
	final int WAIT_BETWEEN_REQUESTS = 1000;
	// API config params
	final String API_URL = Strings.customized("https://api.euskadi.eus/itzuli/{}2{}/v2/translate",
											  FROM_LANG_CODE_PLACEHOLDER, TO_LANG_CODE_PLACEHOLDER);
	final String HEADER_PARAM_API_KEY = "7781e74044e893c3ae5461d97c8394418b9a17847703d8a9fef21c1d3b2d2ab2";
	final String HEADER_PARAM_HTTP_CONTENT_TYPE = "application/json";
	final String BODY_PARAM_M_KEY = "8d9016025eb0a44215c7f69c2e10861d";
	final String BODY_PARAM_MODEL = Strings.customized("generic_{}2{}", 
													   FROM_LANG_CODE_PLACEHOLDER, 
													   TO_LANG_CODE_PLACEHOLDER);

/////////////////////////////////////////////////////////////////////////////////////////
//	TEXT & HTML TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////
	// Mirrors TextTranslatorServiceByDefault behavior
	@Override
	public String translateText(final String text,final Language fromLang,final Language toLang) {
		log.info(Strings.customized("Copying to lang [{}] the text: {}", toLang.getIso639_1(), text));
		return text != null && !text.isEmpty() ? Strings.customized("[{}] {}",
												 toLang.getIso639_1(),text)
											   : "";
	}
	// Translation via external API
	public String translateTextUsingExternalAPI(final String text,final Language fromLang,final Language toLang) {
		log.info(Strings.customized("Using the external API for translation. Translating to lang [{}] the text: {} ", 
									toLang.getIso639_1(), text));
		try {
			String translatedText = translateStringUsingExternalService(text, fromLang, toLang);
			Thread.sleep(WAIT_BETWEEN_REQUESTS);
			log.info(Strings.customized("Text translated using external service: {}" , translatedText));
			return translatedText;
		} catch (Throwable th) {
			log.error(Strings.customized("Error translating text when using external service. Text will be copied instead. Error message:\n{}", 
										 th.getMessage()));
			th.printStackTrace();
			return text != null && !text.isEmpty() ? Strings.customized("[{}] {}",
												 	 toLang.getIso639_1(),text)
												   : "";
		}
	}
	// Mirrors TextTranslatorServiceByDefault behavior
	@Override
	public String translateHTML(final String text,final Language fromLang,final Language toLang) {
		log.info(Strings.customized("Copying to lang [{}] the HTML: {}", toLang.getIso639_1(), text));
		// Add tag indicating target lang, return original text until "Itzuli" functionality is implemented
		return text != null && !text.isEmpty() ? Strings.customized("<p>[{}]</p> {}",
							  					 toLang.getIso639_1(),text)
											   : "";
	}
	// Translation via external API
	public String translateHTMLUsingExternalAPI(final String text,final Language fromLang,final Language toLang) {
		log.info(Strings.customized("Translating to lang [{}] the HTML: {} ", toLang.getIso639_1(), text));
		try {
			String translatedText = translateStringUsingExternalService(text, fromLang, toLang);
			Thread.sleep(WAIT_BETWEEN_REQUESTS);
			log.info(Strings.customized("HTML translated using external service: {}" , translatedText));
			return translatedText;
		} catch (Throwable th) {
			log.error("Error translating HTML when using external service. HTML will be copied instead. Error message:\n{}" + th.getMessage());
			return text != null && !text.isEmpty() ? Strings.customized("[{}] {}",
												 	 toLang.getIso639_1(),text)
												   : "";
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	URL & WEBLINK TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////
	// Mirrors TextTranslatorServiceByDefault behavior
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
	// Translation via external API
	public WebLink translateWebLinkUsingExternalAPI(final WebLink link,final Language fromLang,final Language toLang) {
		WebLink outLink = new WebLink();
		// lang
		outLink.setLanguage(toLang);

		// text, title, tags...
		if (link.getText() != null) 		outLink.setText(this.translateTextUsingExternalAPI(link.getText(),fromLang,toLang));
		if (link.getDescription() != null) 	outLink.setDescription(this.translateTextUsingExternalAPI(link.getDescription(),fromLang,toLang));
		if (link.getTitle() != null)		outLink.setTitle(this.translateTextUsingExternalAPI(link.getTitle(),fromLang,toLang));
		if (CollectionUtils.hasData(link.getTags())) outLink.setTags(link.getTags()
																		 .stream()
																		 .filter(Objects::nonNull)
																		 .map(aTag -> this.translateTextUsingExternalAPI(aTag,fromLang,toLang))
																		 .collect(Collectors.toList()));
		// presentation
		outLink.setPresentation(link.getPresentation());

		// link
		outLink.setUrl(this.translateUrl(link.getUrl(),fromLang,toLang));

		return outLink;
	}
	// Mirrors TextTranslatorServiceByDefault behavior
	@Override
	public Url translateUrl(final Url url, final Language fromLang, final Language toLang) {
		String urlStr = url != null ? url.asString() : null;
		String translatedUrl = this.replaceReferencesOfLangISO639_1(urlStr,fromLang,toLang);
		return translatedUrl != null ? Url.from(translatedUrl) : null;
	}
	// Mirrors TextTranslatorServiceByDefault behavior
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
/////////////////////////////////////////////////////////////////////////////////////////
//	JSON TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////
	public JsonObject translateInBulkUsingExternalAPI(final JsonObject jsonObject, final Language fromLang, final Language toLang) throws Exception {
		// Encoding of json keys to avoid accidental translation by the external
		// service. It also helps with unwanted added whitespace characters
		JsonObject encodedJsonObject = _encodeJsonObjectKeys(jsonObject, false);
		// Divide JSON in chunks.
		// NOTE: It is divided at the attribute level. Attributes over the chunksize 
		// limit will be in chunks that surpass the limit
		List<JsonObject> chunkifiedJson = _chunkifyJSON(encodedJsonObject, MAX_JSON_CHUNK_SIZE);
		// Translate individual chunks
		List<JsonObject> translatedChunkifiedJson = new ArrayList<JsonObject>();
		for (JsonObject chunk: chunkifiedJson) {
			log.info(Strings.customized(
					 "Using the external API for translation. Attempting translation of json chunk to lang[{}]:\n{}",
					 toLang.getIso639_1(), chunk == null ? "" : chunk.toString()));
			// Add preventive line breaks around special characters to minimize JSON
			// malforming during translation
			String translatedChunkStr = "";
			if (chunk != null && chunk.entrySet() != null && !chunk.entrySet().isEmpty()) {
				String preparedChunkStr = _prepareChunkJsonForTranslation(chunk);
				preparedChunkStr = preparedChunkStr.substring(0, preparedChunkStr.length() - 1) + "}";
				// Translate Json chunk as text using external service
				translatedChunkStr = translateTextUsingExternalAPI(preparedChunkStr.toString(), fromLang, toLang);
				// Remove preventive changes
				translatedChunkStr = _unprocessTranslatedString(translatedChunkStr);
			}
			log.info(Strings.customized("Translated json chunk:\n{}",
					translatedChunkStr == null ? "" : translatedChunkStr.toString()));
			try {
				JsonReader reader = new JsonReader(new StringReader(translatedChunkStr));
				reader.setLenient(true);
				JsonElement translatedElement = JsonParser.parseReader(reader);
				if (translatedElement != null && translatedElement.isJsonObject()) {
					translatedChunkifiedJson.add(translatedElement.getAsJsonObject());
				}
				Thread.sleep(WAIT_BETWEEN_REQUESTS);
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					Thread.currentThread().interrupt();
					log.error(Strings.customized("Error while using the external API for translation. An InterruptedException ocurred while sleeping the thread between uses of the API. The untranslated text will be used instead.\nException message:\n{}",
							  e.getMessage()));
				} else if (e instanceof MalformedJsonException || e instanceof EOFException) {
					// Something went wrong with the translation of the json chunk. The untranslated chunk will be used instead.
					log.error(Strings.customized("Error while using the external API for translation. An exception ocurred while trying to parse the translated Json chunk. The untranslated text will be used instead.\nException message:\n{}\nTranslated json chunk content:\n{}",
							  e.getMessage(), translatedChunkStr));
				} else {
					// Something went wrong. An unforeseen exception ocurred. The untranslated chunk will be used instead.
					log.error(Strings.customized("Error while using the external API for translation. An unforesseen exception ocurred. The untranslated text will be used instead.\nException message:\n{}\nTranslated json chunk content:\n{}",
							  e.getMessage(), translatedChunkStr));
				}
				translatedChunkifiedJson.add(chunk);
				throw e;
			}
		}

		// Reconstitute translated json
		JsonObject reconstitutedJSON = new JsonObject();
		for (JsonObject jsonChunkItem : translatedChunkifiedJson) {
			for (Entry<String, JsonElement> entryInChunk : jsonChunkItem.entrySet()) {
				// Json keys are encoded in Base64 to avoid accidental translation by the external service
				reconstitutedJSON.add(entryInChunk.getKey(), entryInChunk.getValue());
			}
		}
		// Decode translated json's keys to its original state
		reconstitutedJSON = _encodeJsonObjectKeys(reconstitutedJSON, true);
		return reconstitutedJSON;
	}
	
	private String _prepareChunkJsonForTranslation(JsonObject chunk) {
		// Open json statement
		String preparedChunkStr = "{";
		for (String key : chunk.keySet()) {
			// All key strings are treated to minimize mistranslation o Json structure
			preparedChunkStr += _processStringForTranslation(Strings.customized("\"{}\":", key));
			// To minimize mistranslation, Json entry strings with html are not treated, while the rest will be.
			if (_hasHTMLTags(chunk.get(key).toString())) {
				preparedChunkStr += Strings.customized("\n\"\n{}\n\"\n,", StringEscapeUtils.unescapeHTML(chunk.get(key).getAsString().replace("\"", "\\\"")));
			} else {
				String preparedValueStr = _processStringForTranslation(Strings.customized("{},", chunk.get(key)));
				preparedChunkStr += preparedValueStr;
			}
		}
		// Delete last comma and close json
		preparedChunkStr = preparedChunkStr.substring(0, preparedChunkStr.length() - 1) + "\n}";
		return preparedChunkStr;
	}
	
	private String _processStringForTranslation(String str) {
		return str.replace("{", "\n{\n")
				  .replace("}", "\n}\n")
				  .replace("[", "\n[\n")
				  .replace("]", "\n]\n")
				  .replace(":", "\n:\n")
				  .replace("&quot;",_encodeKey("&quot;"))
				  .replace("\"","\n\"\n");
	}
	
	private String _unprocessTranslatedString(String str) {
		return str.replace("\n", "")
				  .replace("_JnF1b3Q7_",_decodeKey("_JnF1b3Q7_"));
	}
	
	private boolean _hasHTMLTags(String text) {
		String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
		Pattern pattern = Pattern.compile(HTML_PATTERN);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}
	private static List<JsonObject> _chunkifyJSON(JsonObject jsonObject, int maxChunkSize) {
		// Empty Json starting length. Includes opening and closing brackets, minus last 
		// comma of the attributes that will be added.
		int accumChunkLength = 1;
		List<JsonObject> chunkifiedJson = new ArrayList<>();
		JsonObject jsonChunk = new JsonObject();
		chunkifiedJson.add(jsonChunk);
		for (Entry<String, JsonElement> jsonItem : jsonObject.entrySet()) {
			// jsonItem length, includes key, value and other characters in line like semicolon, quotes and last comma.
			int jsonItemLength = (jsonItem.getKey().toString().length() + jsonItem.getValue().toString().length() + 4);
			accumChunkLength += jsonItemLength;
			if (accumChunkLength >= maxChunkSize) {
				// Max chunk length surpassed. A new chunk will be created, and current jsonItem will be added to it.
				jsonChunk = new JsonObject();
				accumChunkLength = 1 + jsonItemLength;
				chunkifiedJson.add(jsonChunk);
			}
			jsonChunk.add(jsonItem.getKey(), jsonItem.getValue());
		}
		return chunkifiedJson;
	}
	
	// Recursive encoding/decoding of json keys in json object
	private static JsonObject _encodeJsonObjectKeys(final JsonObject jsonToEncode, final boolean reverseEncoding) {
		JsonObject encodedJson = new JsonObject();
		for (Entry<String, JsonElement> jsonEntry : jsonToEncode.entrySet()) {
			if (jsonEntry.getValue().isJsonObject()) {
				encodedJson.add(reverseEncoding ? _decodeKey(jsonEntry.getKey()) : _encodeKey(jsonEntry.getKey()),
								_encodeJsonObjectKeys(jsonEntry.getValue().getAsJsonObject(), reverseEncoding));
			} else if (jsonEntry.getValue().isJsonArray()) {
				encodedJson.add(reverseEncoding ? _decodeKey(jsonEntry.getKey()) : _encodeKey(jsonEntry.getKey()),
								_encodeJsonArrayKeys(jsonEntry.getValue().getAsJsonArray(), reverseEncoding));
			} else {
				encodedJson.add(reverseEncoding ? _decodeKey(jsonEntry.getKey()) : _encodeKey(jsonEntry.getKey()),
								jsonEntry.getValue());
			}
		}
		return encodedJson;
	}
	
	// Recursive encoding/decoding of json keys in json array
	private static JsonArray _encodeJsonArrayKeys(JsonArray jsonArrayToEncode, final boolean decode) {
		JsonArray encodedJsonArray = new JsonArray();
		for (JsonElement jsonArrayItem : jsonArrayToEncode) {
			if (jsonArrayItem.isJsonObject()) {
				jsonArrayItem = _encodeJsonObjectKeys(jsonArrayItem.getAsJsonObject(), decode);
			} else if (jsonArrayItem.isJsonArray()) {
				jsonArrayItem = _encodeJsonArrayKeys(jsonArrayItem.getAsJsonArray(), decode);
			}
			encodedJsonArray.add(jsonArrayItem);
		}
		return encodedJsonArray;
	}
	
	private static String _encodeKey(String key) {
		// Encode keys to Base64, surrounded with underscore characters, to minimize the chances of an accidental translation
		return Strings.customized("_{}_", StringEncodeUtils.encodeBase64AsString(key.getBytes(StandardCharsets.UTF_8)));
	}
	
	private static String _decodeKey(String key) {
		// Sanitize key from illegal characters prior to decoding, such as whitespace, and the surrounding underscore characters
		String sanitizedKey = key.replace(" ", "");
		sanitizedKey = sanitizedKey.substring(1, sanitizedKey.length() - 1);
		// Decode keys from Base64, after discarding the surrounding underscore characters
		return new String(StringEncodeUtils.decodeBase64(sanitizedKey).toString());
	}

	private static Pattern _createLangISO639_1_InUrlPatternFor(final Language replaced) {
		// Match the lang iso639_1 code in an url like:
		// 		http://my-site.en/en/foo-en-bar/thisenshouldnotmatch/enneithershouldthis/en
		//                      *  *      *          X                X                   *
		return Pattern.compile(Strings.customized("(?<=[\\.\\/\\-_=\\$])({})(?:[\\.\\/\\-_\\$])" + // Matching within text. Special characters should flank the ISO639_1 lang string
												  "|(?<=[\\.\\/\\-_=\\$])({})$",					  // Matching at the end of text. A special character should precede the ISO639_1 lang string
												  replaced.getIso639_1(), replaced.getIso639_1()));
	}


	public String translateStringUsingExternalService(final String textToTranslate,final Language fromLang,final Language toLang) throws IOException {

		ExternalTranslationServiceParams params = new ExternalTranslationServiceParams(API_URL.replace(FROM_LANG_CODE_PLACEHOLDER,fromLang.getIso639_1())
																							  .replace(TO_LANG_CODE_PLACEHOLDER,toLang.getIso639_1()),
																					   HEADER_PARAM_API_KEY,
																					   HEADER_PARAM_HTTP_CONTENT_TYPE,
																					   BODY_PARAM_M_KEY,
																					   BODY_PARAM_MODEL.replace(FROM_LANG_CODE_PLACEHOLDER,fromLang.getIso639_1())
																									   .replace(TO_LANG_CODE_PLACEHOLDER,toLang.getIso639_1()));
		HttpRequest  post = params.generateHTTPPostFromParams(textToTranslate);
		if (post == null) {
			return null;
		}
		try {
			String translatedText = "";
			HttpResponse<String> response =  HttpClient.newBuilder()
					                                   .build()
						                                   .send(post,
						                                         HttpResponse.BodyHandlers.ofString());
		    String responseEntityString = response.body();
			JsonObject jsonObject = JsonParser.parseString(responseEntityString)
				                              .getAsJsonObject();
			translatedText = jsonObject.get("translation").getAsString();
			return translatedText;
		} catch (final Throwable jsonException) {
			log.error(Strings.customized("Error while trying to translate a string using an external API (Url: {}). "
										 + "The exception occurs when parsing the response entity as JSON or extracting the key \"message\". "
										 + "Response data:\n{}",
										 params.getApiURL(),
										 jsonException.getLocalizedMessage()));
			throw new IOException(jsonException);
		}
	}


	@Accessors(prefix="_")
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private class ExternalTranslationServiceParams {
		@Getter private final String _apiURL;
		@Getter private final RequestParam _headerParamApiKey;
		@Getter private final RequestParam _headerParamHTTPContentType;
		@Getter private final RequestParam _bodyParamMKey;
		@Getter private final RequestParam _bodyParamModel;
		@Getter private RequestParam _bodyParamText;

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		private class RequestParam {
			@Getter final private String _name;
			@Getter final private String _value;
		}
		public ExternalTranslationServiceParams (final String apiURL,
												 final String headerParamApiKey,
												 final String headerParamHTTPContentType,
												 final String bodyParamMKey,
												 final String bodyParamModel
		) {
			_apiURL = apiURL;
			_headerParamApiKey = new RequestParam("apiKey", headerParamApiKey);
			_headerParamHTTPContentType = new RequestParam("Content-Type", headerParamHTTPContentType);
			_bodyParamMKey = new RequestParam("mkey", bodyParamMKey);
			_bodyParamModel = new RequestParam("model", bodyParamModel);
		}


		public HttpRequest  generateHTTPPostFromParams(final String textToTranslate) throws UnsupportedEncodingException {
			if (textToTranslate == null) {
				return null;
			}
			JsonObject requestBodyJSON = new JsonObject();
			requestBodyJSON.addProperty(this.getBodyParamMKey().getName(), this.getBodyParamMKey().getValue());
			requestBodyJSON.addProperty(this.getBodyParamModel().getName(), this.getBodyParamModel().getValue());
			requestBodyJSON.addProperty("text", textToTranslate);
			String payloadAsString =  requestBodyJSON.toString();
			HttpRequest request = HttpRequest.newBuilder()
											  .uri(URI.create(this.getApiURL()))
											  .header(this.getHeaderParamApiKey().getName(), this.getHeaderParamApiKey().getValue())
											  .header(this.getHeaderParamHTTPContentType().getName(), this.getHeaderParamHTTPContentType().getValue())
											  .POST(HttpRequest.BodyPublishers.ofString(payloadAsString))
											  .build();
			return request;
		}

	}
}
