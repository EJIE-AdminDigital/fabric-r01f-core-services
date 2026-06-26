package r01f.cloud.aws.s3.model;

import r01f.enums.EnumExtended;

public enum AWSS3RequestedOperation
 implements EnumExtended<AWSS3RequestedOperation> {
	HEAD,
	GET,
	PUT,
	DELETE,
	COPY;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3RequestedOperation fromName(final String name) {
		return EnumExtended.fromName(name,
									 AWSS3RequestedOperation.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + AWSS3RequestedOperation.class + " element with name=" + name));
	}
}
