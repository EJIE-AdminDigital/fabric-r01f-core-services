package r01f.cloud.aws.s3.model;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;


@Immutable
@NoArgsConstructor
public class AWSS3RequestID
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = -1370608038411925267L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public AWSS3RequestID(final String id) {
		super(id);
	}
	public static AWSS3RequestID forId(final String idAsString) {
		return new AWSS3RequestID(idAsString);
	}
}