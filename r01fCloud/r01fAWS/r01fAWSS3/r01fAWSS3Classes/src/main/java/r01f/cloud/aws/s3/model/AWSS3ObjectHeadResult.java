package r01f.cloud.aws.s3.model;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;


@Accessors(prefix="_")
public class AWSS3ObjectHeadResult
	 extends AWSS3RequestResultBase<AWSS3ObjectHeadResult> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private long _contentLength = -1;
	@Getter @Setter private Charset _charset;
	@Getter @Setter private MimeType _mimeType;
	@Getter @Setter private Date _expiresAt;
	@Getter @Setter private Date _lastModified;
	@Getter @Setter private AWSS3ObjectVersionID _versionId;
	@Getter @Setter private AWSS3ObjectETag _eTag;
	@Getter @Setter private boolean _deleteMarker;
	@Getter @Setter protected Collection<AWSS3ObjectMetaDataItem> _customMetadata;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectHeadResult(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		super(bucket,key,
			  AWSS3RequestedOperation.HEAD);
	}
	public static AWSS3ObjectHeadResultBuilderStep fromHeadResponseOn(final AWSS3Bucket bucket,final AWSS3ObjectKey key) {
		AWSS3ObjectHeadResult res = new AWSS3ObjectHeadResult(bucket,key);
		return res.new AWSS3ObjectHeadResultBuilderStep();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3ObjectHeadResultBuilderStep {
		
		/*
		 * When 404 error (Not-Exists) only return bucket and key
		 */
		public AWSS3ObjectHeadResult notExists() {
			return AWSS3ObjectHeadResult.this;
		}
		
		public AWSS3ObjectHeadResult with(final HeadObjectResponse headRes) {
			_contentLength = headRes.contentLength();
			if (headRes.expires() != null) {
				_expiresAt = Date.from(headRes.expires());
			}
			if (headRes.lastModified() != null) {
				_lastModified = Date.from(headRes.lastModified());
			}
			if (Strings.isNOTNullOrEmpty(headRes.contentType())) {
				_mimeType = MimeTypes.forName(headRes.contentType());
			}
			if (Strings.isNOTNullOrEmpty(headRes.contentEncoding())) {
				_charset = Charset.forName(headRes.contentEncoding());
			}
			if (Strings.isNOTNullOrEmpty(headRes.versionId())) {
				_versionId = AWSS3ObjectVersionID.forId(headRes.versionId());
			}
			if (Strings.isNOTNullOrEmpty(headRes.eTag())) {
				_eTag = AWSS3ObjectETag.forId(headRes.eTag());
			}
			if (headRes.deleteMarker() != null ) {
		 	 _deleteMarker = headRes.deleteMarker();
			}
			// custom items
			if (CollectionUtils.hasData(headRes.metadata())) {
				_customMetadata = headRes.metadata()
										 .keySet()
										 .stream()
										 .map(id -> new AWSS3ObjectMetaDataItem(AWSS3ObjectMetadataItemId.forId(id),
															                    headRes.metadata().get(id)))
										 .collect(Collectors.toList());
			}
			return AWSS3ObjectHeadResult.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
 		StringBuilder dbg = new StringBuilder(super.debugInfo());
 		return dbg;
	}
}
