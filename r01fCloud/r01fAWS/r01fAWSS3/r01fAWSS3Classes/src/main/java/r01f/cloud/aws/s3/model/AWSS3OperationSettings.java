
package r01f.cloud.aws.s3.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.cloud.aws.s3.AWSS3ProgressListener;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;


@NoArgsConstructor
@Accessors(prefix="_")
public class AWSS3OperationSettings
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private AWSS3TransferSettings _tranferConfig;
    @Getter @Setter private AWSS3ProgressListener _progressListener;
    @Getter @Setter private boolean _wait;
 /////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
 	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
	    outDbgInfo.append("Upload Config :{ \n ").append(Strings.customized(" Transfer Config {}",_tranferConfig.debugInfo() ));
	    outDbgInfo.append("\n").append(Strings.customized("_wait {}", _wait));
		outDbgInfo.append(" } \n")	;
	    return outDbgInfo.toString();
	}
}

