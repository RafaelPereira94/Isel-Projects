package pt.isel.daw.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import pt.isel.daw.model.Class;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassMapper implements RowMapper<Class> {

    @Override
    public Class mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Class(rs.getString("id"),
                rs.getInt("max"),
                rs.getString("semester"),
                rs.getString("course"));
    }

}
