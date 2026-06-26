package r01f.cloud.aws.s3.model;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.VersionID;


@Immutable
@NoArgsConstructor
public class AWSS3ObjectVersionID
	 extends OIDBaseMutable<String> 
  implements VersionID {

	private static final long serialVersionUID = 6206666360461357289L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectVersionID(final String id) {
		super(id);
	}
	public static AWSS3ObjectVersionID forId(final String idAsString) {
		return new AWSS3ObjectVersionID(idAsString);
	}
}