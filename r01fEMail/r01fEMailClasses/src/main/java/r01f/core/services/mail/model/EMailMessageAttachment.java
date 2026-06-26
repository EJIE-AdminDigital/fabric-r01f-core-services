package r01f.core.services.mail.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.mime.MimeBodyPartDispositionType;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;

@Accessors(prefix="_")
public class EMailMessageAttachment
  implements Serializable {

	private static final long serialVersionUID = -3953489838599622315L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final String _name;
	@Getter private final byte[] _data;
	@Getter private final MimeType _contentType;
	@Getter private final MimeBodyPartDispositionType _disposition;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public EMailMessageAttachment(final String name,
								  final byte[] data,
								  final MimeType contentType) {
		this(name, data, contentType, null);
	}
	public EMailMessageAttachment(final String name,
								  final byte[] data,
								  final MimeType contentType,
								  final MimeBodyPartDispositionType disposition) {
		_name = name;
		_data = data;
		_contentType = contentType;
		_disposition = disposition;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MimeType getContentTypeOrDefault(final MimeType defContentType) {
		return _contentType != null ? _contentType
									: defContentType != null ? defContentType : MimeTypes.OCTECT_STREAM;
	}

	public MimeBodyPartDispositionType getDispositionTypeOrDefault(final MimeBodyPartDispositionType defDispositionType) {
		return _disposition != null ? _disposition
									: defDispositionType != null ? defDispositionType : MimeBodyPartDispositionType.DISPOSITION_ATTACHMENT;
	}
}
