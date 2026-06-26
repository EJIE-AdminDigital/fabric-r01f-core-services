package r01f.cloud.nexmo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;

public abstract class MessageContents {

/////////////////////////////////////////////////////////////////////////////////////////
//	Message Content Text Type
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="content",typeId="text")
	@Accessors(prefix="_")
	public static class TextMessageContent 
				extends MessageContentBase {		
		@MarshallField(as="text")
	    @Setter @Getter private  String  _text;
		
		public TextMessageContent() {
			super();			
		}		
		public TextMessageContent(@MarshallFrom("text") final String text) {
			super(MessageContentType.text);	
			_text = text;
		}	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Message Content Text Image
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="content",typeId="image")
	@Accessors(prefix="_")
	public static class ImageMessageContent 
			    extends MessageContentBase {
		
		@MarshallField(as="url")
		@Setter @Getter private  Url  _url;
		
		public ImageMessageContent() {
			super();			
		}
		public ImageMessageContent(@MarshallFrom("url") final Url url) {
			super(MessageContentType.image);	
			_url = url;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Message Content Text Image
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="content",typeId="location")
	@Accessors(prefix="_")
	public static class LocationMessageContent 
				extends MessageContentBase {
	
		@MarshallField(as="lattitude")
		@Setter @Getter private  Long  _lattitude;
		
		@MarshallField(as="longitude")
		@Setter @Getter private  Long  _longitude;
		
		public LocationMessageContent() {
			super();			
		}
		public LocationMessageContent(@MarshallFrom("lat") final Long lattitude,@MarshallFrom("long") final Long longitude) {
			super(MessageContentType.location);	
			_lattitude = lattitude;
			_longitude = longitude;
		}
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	Message Content Text Image
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="content",typeId="audio")
	@Accessors(prefix="_")
	public static class AudioMessageContent 
				extends MessageContentBase {
	
		@MarshallField(as="url")
		@Setter @Getter private  Url  _url;
		
		public AudioMessageContent() {
			super();			
		}
		public AudioMessageContent(@MarshallFrom("url") final Url url) {
			super(MessageContentType.audio);	
			_url = url;
		}
	}	
}
