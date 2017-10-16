package pt.isel.daw.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import pt.isel.daw.model.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Student(
                rs.getInt("number"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("id"),
                rs.getString("password")
        );
    }
}
