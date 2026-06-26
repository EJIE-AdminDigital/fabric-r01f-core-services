package r01f.cloud.aws.s3.migration.model;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.OIDs;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MigrationCommonOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor(access=AccessLevel.PRIVATE)
	private abstract class MigrationGUIDDispenser {
	/////////////////////////////////////////////////////////////////////////////////////////
	// 	GUID GENERATION
	/////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * Generates a GUID
		 * @return
		 */
		public static String generateGUID() {
			return OIDs.supplyOid();
		}
	}
	/**
	 * Base OID for every R01M OID
	 */
	@Immutable
	protected static abstract class MigrationModelObjectOIDBase<SELF_TYPE extends MigrationModelObjectOIDBase<SELF_TYPE>>
	              		 extends OIDBaseMutable<String> 	// usually this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
					  implements OIDTyped<String> {

		private static final long serialVersionUID = 4449032738491944411L;

		public MigrationModelObjectOIDBase() {
		}
		public MigrationModelObjectOIDBase(final String id) {
			super(id);
		}
		@Override
		public boolean isValid() {
			return super.isValid() && this.getId().length() <= OID.OID_LENGTH;
		}
		/**
		 * Generates an oid
		 * @return the generated oid
		 */
		public static String supplyId() {
			return MigrationGUIDDispenser.generateGUID();
		}
		private static final transient Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[^%]+%");
		/**
		 * Checks if the oid is a placeholder (something like a var)
		 * ie: %R01_PORTALID%
		 * @return true if it's a placeholder, false otherwise
		 */
		public boolean isPlaceHolder() {
			return PLACEHOLDER_PATTERN.matcher(this.getId().toString()).matches();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class MigrationOID extends MigrationModelObjectOIDBase<MigrationOID> {
		
		private static final long serialVersionUID = 1L;

		public MigrationOID(final String id) {
			super(id);
		}
	}
}
