package utils;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.IntervalType;
import org.hibernate.query.sqm.TemporalUnit;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.SqlTypes;

import java.sql.Types;
import java.time.Duration;
import java.time.Period;

public class SqlDialect extends Dialect {

    public SqlDialect() {
        this(DatabaseVersion.make(3, 0));
    }

    public SqlDialect(DatabaseVersion version) {
        super(version);
    }

    @Override
    public int getDefaultStatementBatchSize() {
        return 100;
    }

    @Override
    protected String columnType(int sqlTypeCode) {
        return switch(sqlTypeCode) {
            case SqlTypes.BOOLEAN -> "integer";
            case SqlTypes.TINYINT -> "tinyint";
            case SqlTypes.SMALLINT -> "smallint";
            case SqlTypes.INTEGER -> "integer";
            case SqlTypes.BIGINT -> "bigint";
            case SqlTypes.FLOAT -> "float";
            case SqlTypes.DOUBLE -> "double";
            case SqlTypes.DECIMAL -> "decimal";
            case SqlTypes.CHAR -> "char";
            case SqlTypes.VARCHAR -> "varchar";
            case SqlTypes.LONGNVARCHAR, SqlTypes.LONGVARCHAR -> "text";
            case SqlTypes.DATE -> "date";
            case SqlTypes.TIME -> "time";
            case SqlTypes.TIMESTAMP -> "timestamp";
            case SqlTypes.BLOB -> "blob";
            case SqlTypes.CLOB -> "clob";
            default -> super.columnType(sqlTypeCode);
        };
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "SELECT '';";
    }


    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean hasAlterTable() {
        return false; // As specified in NHibernate dialect
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }



    @Override
    public boolean supportsTemporaryTables() {
        return true;
    }

    @Override
    public String getSelectGUIDString() {
        return "select hex(randomblob(16))";
    }

    @Override
    public boolean supportsUnboundedLobLocatorMaterialization() {
        return false;
    }

    @Override
    public boolean supportsSubselectAsInPredicateLHS() {
        return true;
    }

}