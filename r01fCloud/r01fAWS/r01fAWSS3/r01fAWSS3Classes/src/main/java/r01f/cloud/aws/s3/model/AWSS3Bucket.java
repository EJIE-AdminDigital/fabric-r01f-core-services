package r01f.cloud.aws.s3.model;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;


@Immutable
@Accessors(prefix="_")
@NoArgsConstructor
public class AWSS3Bucket
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 4162366466990455545L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AWSS3Bucket(final String id) {
		super(id);
	}
	public static AWSS3Bucket forId(final String idAsString) {
		return new AWSS3Bucket(idAsString);
	}
	public static <O extends OID> AWSS3Bucket of(final O oid) {
		return new AWSS3Bucket(oid.asString());
	}
}