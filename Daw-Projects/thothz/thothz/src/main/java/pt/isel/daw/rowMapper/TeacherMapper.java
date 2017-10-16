package pt.isel.daw.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import pt.isel.daw.model.Teacher;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        boolean admin = rs.getString("admin").equals("T");
        return new Teacher(
                rs.getInt("number"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("id"),
                admin,
                rs.getString("password")
        );
    }
}
