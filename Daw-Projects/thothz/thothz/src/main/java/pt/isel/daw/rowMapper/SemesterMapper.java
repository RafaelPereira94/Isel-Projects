package pt.isel.daw.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import pt.isel.daw.model.Semester;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SemesterMapper implements RowMapper<Semester> {
    @Override
    public Semester mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Semester(
                rs.getString("name"),
                rs.getInt("year"),
                rs.getString("season")
        );
    }
}
