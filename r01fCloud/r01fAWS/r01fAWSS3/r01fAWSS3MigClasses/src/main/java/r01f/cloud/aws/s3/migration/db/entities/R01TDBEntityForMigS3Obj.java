package r01f.cloud.aws.s3.migration.db.entities;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;

/**
 * Represents a database entity for tracking S3 migration operations.
 * This entity stores information about individual migration tasks, including
 * source and destination paths, and any warnings or errors encountered.
 *
 * <p>
 * This class is mapped to the database table {@code R01TMIGS3T00} and is designed
 * to be used within a JPA persistence context. It is not cacheable to ensure
 * that the latest state of migration records is always retrieved from the database.
 * </p>
 *
 * <p>
 * The {@code @Accessors(prefix="_")} annotation from Lombok configures the getters and setters
 * to automatically handle fields prefixed with an underscore.
 * </p>
 */
@Entity @Cacheable(false) 
@Table(name = "R01TMIGS3OBJT00")
@NamedQueries({})
@Accessors(prefix="_")
@NoArgsConstructor
public class R01TDBEntityForMigS3Obj implements Serializable {
	private static final long serialVersionUID = 1888746153862955918L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
     * The OID of the overall migration operation to which this individual record belongs.
     * This field links individual S3 object migrations to a broader migration task.
     * It is a non-nullable column with a predefined length.
     */
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _oid;
	/**
     * The OID of the overall migration operation to which this individual record belongs.
     * This field links individual S3 object migrations to a broader migration task.
     * It is a non-nullable column with a predefined length.
     */
	@Column(name="MIGRATION_OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _migrationOid;
	/**
     * The source path of the S3 object being migrated.
     * This column stores the original location of the object.
     * It is a non-nullable column with a maximum length of 256 characters.
     */
	@Column(name="SOURCE_PATH",length=256,nullable=false) @Basic
	@Getter @Setter protected String _sourcePath;
	/**
     * The local tmp path of the object being migrated.
     * This column stores the tmp location of the object.
     * It is a non-nullable column with a maximum length of 256 characters.
     */
	@Column(name="LOCAL_PATH",length=256,nullable=false) @Basic
	@Getter @Setter protected String _localPath;
	/**
     * The destination path of the S3 object after migration.
     * This column stores the new location of the object.
     * It is a non-nullable column with a maximum length of 256 characters.
     */
	@Column(name="DESTINATION_PATH",length=256) @Basic
	@Getter @Setter protected String _destinationPath;
	/**
     * Any warning messages encountered during the migration of this specific S3 object.
     * This field can be null and has a maximum length of 256 characters.
     */
	@Column(name="WARNING",length=256) @Basic
	@Getter @Setter protected String _warning;
	/**
     * Any error messages encountered during the migration of this specific S3 object.
     * This field is stored as a Large Object (LOB) to accommodate potentially long error messages,
     * with a maximum length of 1024 characters. It is eagerly fetched.
     */
	@Column(name="ERROR",length=1024) @Lob @Basic(fetch=FetchType.EAGER)
	@Getter @Setter protected String _error;
}
