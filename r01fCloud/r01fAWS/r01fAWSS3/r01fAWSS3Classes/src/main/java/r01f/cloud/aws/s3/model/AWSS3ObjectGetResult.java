package r01f.cloud.aws.s3.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;


@Accessors(prefix="_")
public class AWSS3ObjectGetResult
	 extends AWSS3RequestResultBase<AWSS3ObjectGetResult>
  implements Closeable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private transient InputStream _inputStream;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectGetResult(final AWSS3BucketID bucket,final AWSS3ObjectKey key) {
		super(bucket,key,
			  AWSS3Operation.GET);
	}
	@SuppressWarnings("resource")
	public static AWSS3ObjectDownloadResultBuilderInputStreamStep fromGetObjectResponseOn(final AWSS3BucketID bucket,final AWSS3ObjectKey key) {
		AWSS3ObjectGetResult res = new AWSS3ObjectGetResult(bucket,key);
		return res.new AWSS3ObjectDownloadResultBuilderInputStreamStep();
	}
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3ObjectDownloadResultBuilderInputStreamStep {

		public AWSS3ObjectGetResult returning(final ResponseInputStream<GetObjectResponse> inputStream) {
			_inputStream = inputStream;
			return AWSS3ObjectGetResult.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLOSEABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void close() throws IOException {
		if (_inputStream != null) _inputStream.close();
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
