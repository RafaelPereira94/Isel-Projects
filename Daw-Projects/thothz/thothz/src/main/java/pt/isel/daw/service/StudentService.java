package pt.isel.daw.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Class;
import pt.isel.daw.model.Student;
import pt.isel.daw.rowMapper.ClassMapper;
import pt.isel.daw.rowMapper.StudentMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class StudentService extends Service{

    private final String SELECT = "SELECT daw.user.name, number, daw.user.email, daw.user.id, daw.user.password " +
            "FROM daw.student INNER JOIN daw.user on daw.student.email = daw.user.email";

    public StudentService(DataSourceTransactionManager transactionManager){
        super(transactionManager);
    }

    public List<Student> getAllStudents(int offset, int limit){
        return getJdbcTemplate()
                .query(SELECT + String.format(PAGINATION, limit, offset), new StudentMapper());
    }

    public Student getStudent(int number) {
        String sql = SELECT + " WHERE number = ?";
        return getJdbcTemplate()
                .queryForObject(sql, new Object[]{number}, new StudentMapper());
    }

    public Student getStudent(String email) {
        List<Student> s = getJdbcTemplate()
                .query(SELECT + " WHERE daw.user.email = ?", new Object[]{email}, new StudentMapper());
        if(s.isEmpty()) return null;
        return s.get(0);
    }

    public int createStudent(int num, String name, String email,String password){
        //criar user e depois estudante
        JdbcTemplate jt = getJdbcTemplate();
        String insertUser, insertStudent = null;
        insertUser = "INSERT INTO daw.user(name, email, password) values (?,?,?);";
        insertStudent = "INSERT INTO daw.student(number, email) values (?,?);";
        jt.update(insertUser, name, email,password);

        return jt.update(insertStudent, num, email);
    }

    public int deleteStudent(int id){
        JdbcTemplate jt = getJdbcTemplate();
        String deleteStudent = "DELETE FROM daw.student where number = ?";
        String deleteUser = "DELETE FROM daw.user where email = ?";

        Student st =  jt.queryForObject(SELECT + " WHERE daw.student.number = ?", new Object[]{id}, new StudentMapper());
        jt.update(deleteStudent, id);
        return jt.update(deleteUser, st.getEmail());
    }

    public List<Class> getClasses(int number){
        List<Class> l = getJdbcTemplate().query(
                "SELECT DISTINCT id, daw.class.course, daw.class.semester, max " +
                        "FROM daw.classStudent INNER JOIN daw.class ON classId = id " +
                        "WHERE studentNumber = ?",
                new Object[]{number},
                new ClassMapper()
        );
        return l;
    }

    public int countStudents() {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.student", Integer.class);
    }
}
