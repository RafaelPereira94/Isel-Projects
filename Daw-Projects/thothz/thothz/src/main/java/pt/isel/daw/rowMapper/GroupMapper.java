package pt.isel.daw.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import pt.isel.daw.model.Group;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Group(rs.getString("classId"),
                rs.getString("course"),
                rs.getString("semester"),
                rs.getInt("number"));
    }

}
