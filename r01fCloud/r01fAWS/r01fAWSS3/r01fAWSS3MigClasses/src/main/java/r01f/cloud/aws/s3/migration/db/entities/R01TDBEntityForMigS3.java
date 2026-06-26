package r01f.cloud.aws.s3.migration.db.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "R01TMIGS3T00")
@NamedQueries({})
@Accessors(prefix="_")
@NoArgsConstructor
public class R01TDBEntityForMigS3 implements Serializable {
	private static final long serialVersionUID = 1888746153862955918L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
     * The OID of the overall migration operation for one remote path.
     * It is a non-nullable column with a predefined length.
     */
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _oid;
	/**
     * Type of remote system: HDFS / Local.
     * It is a non-nullable column with a predefined length.
     */
	@Column(name="MIGRATION_OID",length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _migrationOid;
	/**
     * Type of remote system: HDFS / Local.
     * It is a non-nullable column with a maximum length of 8 characters.
     */
	@Column(name="REMOTE_TYPE",length=8) @Basic
	@Getter @Setter protected String _remoteType;
	/**
     * The remote path of the migration.
     * It is a non-nullable column with a maximum length of 256 characters.
     */
	@Column(name="REMOTE_PATH",length=256,nullable=false) @Basic
	@Getter @Setter protected String _remotePath;
	/**
     * The timestamp when the migration operation started.
     * This column stores the date and time of the beginning of the migration task.
     * It is a non-nullable column.
     */
    @Column(name="START_DATE", nullable=false) @Basic
    @Getter @Setter protected LocalDateTime _startDate;
    /**
     * The timestamp when the migration operation ended.
     * This column stores the date and time of the completion of the migration task.
     * It can be nullable if the migration is still in progress.
     */
    @Column(name="END_DATE") @Basic
    @Getter @Setter protected LocalDateTime _endDate;
}
