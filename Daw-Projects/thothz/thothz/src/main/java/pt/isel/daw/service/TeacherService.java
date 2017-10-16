package pt.isel.daw.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Class;
import pt.isel.daw.model.Teacher;
import pt.isel.daw.rowMapper.ClassMapper;
import pt.isel.daw.rowMapper.TeacherMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class TeacherService extends Service{

    private final String SELECT = "SELECT daw.user.id, daw.user.name, number, daw.user.email, daw.teacher.admin, daw.user.password " +
            "FROM daw.teacher INNER JOIN daw.user on daw.teacher.email = daw.user.email";

    public TeacherService(DataSourceTransactionManager transactionManager){
        super(transactionManager);
    }

    public List<Teacher> getAllTeachers(int offset, int limit) {
        return getJdbcTemplate()
                .query(SELECT + String.format(PAGINATION, limit, offset), new TeacherMapper());
    }

    public Teacher getTeacher(int number) {
        return getJdbcTemplate()
                .queryForObject(SELECT + " WHERE number = ?", new Object[]{number}, new TeacherMapper());
    }

    public Teacher getTeacher(String email) {
        List<Teacher> t = getJdbcTemplate()
                .query(SELECT + " WHERE daw.teacher.email = ?", new Object[]{email}, new TeacherMapper());
        if(t.isEmpty()) return null;
        return t.get(0);
    }

    public int createTeacher(int num, String name, String email,String admin,String password) {
        //criar user e depois estudante
        JdbcTemplate jt = getJdbcTemplate();
        String insertTeacher = null,insertUser;
        insertUser = "INSERT INTO daw.user(name,email,password) values (?,?,?);";
        insertTeacher = "INSERT INTO daw.teacher(number,email,admin) values (?,?,?);";
        jt.update(insertUser, name, email,password);
        return jt.update(insertTeacher, num, email,admin);
    }

    public int deleteTeacher(int id){
        JdbcTemplate jt = getJdbcTemplate();
        String deleteTeacher = "DELETE FROM daw.teacher WHERE number = ?";
        String deleteUser = "DELETE FROM daw.user where email = ?";
        Teacher t =  jt.queryForObject(SELECT + " WHERE daw.teacher.number = ?", new Object[]{id}, new TeacherMapper());
        jt.update(deleteTeacher, id);
        return jt.update(deleteUser, t.getEmail());
    }

    public List<Class> getClasses(int number){
        return getJdbcTemplate().query(
                "SELECT DISTINCT id, daw.class.course, daw.class.semester, max " +
                        "FROM daw.classTeacher INNER JOIN daw.class ON classId = id " +
                        "WHERE teacherNumber = ?",
                new Object[]{number},
                new ClassMapper()
        );
    }

    public int countTeachers() {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.teacher", Integer.class);
    }
}
