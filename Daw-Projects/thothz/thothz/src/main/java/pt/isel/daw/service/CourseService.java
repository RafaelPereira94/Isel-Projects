package pt.isel.daw.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Course;
import pt.isel.daw.model.Semester;
import pt.isel.daw.rowMapper.CourseMapper;
import pt.isel.daw.rowMapper.SemesterMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class CourseService extends Service{

    private final String SELECT =  "SELECT name, acronym, coordinator FROM daw.course ";

    public CourseService(DataSourceTransactionManager transactionManager){
        super(transactionManager);
    }

    public List<Course> getAllCourses(int offset, int limit) {
        return getJdbcTemplate()
                .query(SELECT + String.format(PAGINATION, limit, offset), new CourseMapper());
    }

    public Course getCourse(String name) {
        String path = SELECT + "WHERE name = ?";
        return getJdbcTemplate().queryForObject(path, new Object[]{name}, new CourseMapper());
    }

    public List<Course> getCoursesByName(String name,int offset,int limit){
        String path = SELECT + "WHERE name = ?"+String.format(PAGINATION,limit,offset);
        return getJdbcTemplate()
                .query(path,new Object[]{name},new CourseMapper());
    }

    public int createCourse(String name, String acronym, int coordinator) {
        JdbcTemplate jt = getJdbcTemplate();
        String insertCourse = "INSERT INTO daw.course(name,acronym,coordinator) values (?,?,?);";
        return jt.update(insertCourse,name,acronym,coordinator);
    }

    public int deleteCourse(String name) {
        JdbcTemplate jt = getJdbcTemplate();
        String deleteCouse = "DELETE FROM daw.course WHERE name = ?;";
        return jt.update(deleteCouse,name);
    }

    public Semester getSemesters(String name) {
        String query = "select name,year,season from daw.semester where name = (SELECT semester FROM daw.course join daw.class on(name = course) where name = ?)";
        return getJdbcTemplate().queryForObject(query,new Object[]{name},new SemesterMapper());
    }

    public int countCourses() {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.course", Integer.class);
    }

    private int getCourseCoordinatorNumber(String courseName){
        return getCourse(courseName).getCoordinator();
    }

    public boolean isCourseCoordinator(String courseName, int teacherNumber){
        return getCourseCoordinatorNumber(courseName) == teacherNumber;
    }
}