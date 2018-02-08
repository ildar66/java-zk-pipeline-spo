package ru.md.persistence.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class YesNoBooleanTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, convert(parameter));
    }

    @Override
    public Boolean getNullableResult(java.sql.ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getString(columnName));
    }

    @Override
    public Boolean getNullableResult(java.sql.ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getString(columnIndex));
    }
 
    private String convert(Boolean b) {
        if (b == null)
            return null;
        
        return b ? "y" : "n";
    }

    private Boolean convert(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        
        return "y".equalsIgnoreCase(s);
    }
}
