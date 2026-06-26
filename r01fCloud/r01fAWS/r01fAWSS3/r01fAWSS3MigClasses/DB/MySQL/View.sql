CREATE VIEW view_migration_obj AS
SELECT
    OID,
    MIGRATION_OID,
    SOURCE_PATH,
    LOCAL_PATH,
    DESTINATION_PATH,
    WARNING,
    ERROR
FROM
    pci.r01tmigs3objt00 AS _rtt_
ORDER BY
    _rtt_.MIGRATION_OID DESC,
    _rtt_.SOURCE_PATH ASC;
CREATE VIEW view_migration AS
SELECT
    m.OID,
    m.MIGRATION_OID AS MIGRATION_OID,
    m.REMOTE_TYPE,
    m.REMOTE_PATH,
    m.START_DATE,
    m.END_DATE,
    COUNT(CASE WHEN o.ERROR IS NOT NULL AND TRIM(o.ERROR) <> '' THEN 1 END) AS NUM_ERRORS,
    COUNT(CASE WHEN o.WARNING IS NOT NULL AND TRIM(o.WARNING) <> '' THEN 1 END) AS NUM_WARNINGS
FROM
    R01TMIGS3T00 m
LEFT JOIN
    R01TMIGS3OBJT00 o ON m.MIGRATION_OID = o.MIGRATION_OID
GROUP BY
    m.OID,
    m.MIGRATION_OID,
    m.REMOTE_TYPE,
    m.REMOTE_PATH
ORDER BY
    m.MIGRATION_OID ASC;
--select * from r01tmigs3t00;
--select count(*) from view_migration_obj;
--select * from view_migration_obj where ERROR is not null or WARNING is not null;