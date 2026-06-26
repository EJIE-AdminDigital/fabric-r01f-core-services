package r01f.cloud.aws.s3.model;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;


@Immutable
@NoArgsConstructor
public class AWSS3ObjectMetadataItemId
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = 4162366466990455545L;

	public AWSS3ObjectMetadataItemId(final String id) {
		super(id);
	}
	public static AWSS3ObjectMetadataItemId forId(final String idAsString) {
		return new AWSS3ObjectMetadataItemId(idAsString);
	}
}