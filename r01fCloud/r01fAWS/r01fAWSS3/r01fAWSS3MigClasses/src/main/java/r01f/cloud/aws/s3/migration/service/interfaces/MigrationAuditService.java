package r01f.cloud.aws.s3.migration.service.interfaces;

import r01f.cloud.aws.s3.migration.model.AuditModel;

public interface MigrationAuditService {
	/**
     * Audit a successful migration.
     * 
     * @param audit model to audit the migration
     */
	public void auditSuccess(AuditModel audit);
	/**
     * Audit a successful migration.
     * 
     * @param audit model to audit the migration
     */
	public void auditWarning(AuditModel audit);
	/**
     * Audit a successful migration.
     * 
     * @param audit model to audit the migration
     */
	public void auditError(AuditModel audit);
	
}