package r01f.cloud.aws.s3.model;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;


@Immutable
@NoArgsConstructor
public class AWSS3ObjectETag
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 7085102091342149546L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3ObjectETag(final String id) {
		super(id);
	}
	public static AWSS3ObjectETag forId(final String idAsString) {
		return new AWSS3ObjectETag(idAsString);
	}
}