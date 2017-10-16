package pt.isel.daw.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Semester;
import pt.isel.daw.rowMapper.SemesterMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class SemesterService extends Service{

    private final String SELECT = "SELECT name, year, season FROM daw.semester";

    public SemesterService(DataSourceTransactionManager transactionManager) {
        super(transactionManager);
    }

    public List<Semester> getAllSemesters(int offset, int limit){
        return getJdbcTemplate()
                .query(SELECT + String.format(PAGINATION, limit, offset), new SemesterMapper());
    }

    public Semester getSemester(String name){
        return getJdbcTemplate()
                .queryForObject(SELECT + " WHERE name = ?", new Object[]{name}, new SemesterMapper());
    }

    public int postSemester(String name, int year, String season){
        return getJdbcTemplate()
                .update("INSERT INTO daw.semester(name, year, season) VALUES (?, ?, ?)", name, year,season);
    }

    public int deleteSemester(String name){
        return getJdbcTemplate()
                .update("DELETE FROM daw.semester WHERE name = ?", name);
    }

    public int countSemesters() {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.semester", Integer.class);
    }
}
