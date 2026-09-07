package r01f.cloud.aws.s3.model;

import r01f.enums.EnumExtended;

public enum AWSS3Operation
 implements EnumExtended<AWSS3Operation> {
	HEAD,
	GET,
	PUT,
	DELETE,
	COPY;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3Operation fromName(final String name) {
		return EnumExtended.fromName(name,
									 AWSS3Operation.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + AWSS3Operation.class + " element with name=" + name));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3Operation fromEventString(final String eventStr) {
        if (eventStr == null || eventStr.isBlank()) {
            throw new IllegalArgumentException("null event");
        }
        
        String normalized = eventStr.toLowerCase();
        if (normalized.contains("put") || 
            normalized.contains("post") || 
            normalized.contains("completemultipartupload") || 
            normalized.contains("object created")) {
            return PUT;
        }

        if (normalized.contains("copy")) {
            return COPY;
        }

        if (normalized.contains("delete") || 
            normalized.contains("object deleted") || 
            normalized.contains("lifecycle")) {
            return DELETE;
        }

        if (normalized.contains("get")) {
            return GET;
        }

        if (normalized.contains("head")) {
            return HEAD;
        }
        throw new IllegalArgumentException("..invalid opertation :" + eventStr);
    }

}
