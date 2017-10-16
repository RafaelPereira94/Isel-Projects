package pt.isel.daw.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Group;
import pt.isel.daw.model.Student;
import pt.isel.daw.rowMapper.GroupMapper;
import pt.isel.daw.rowMapper.StudentMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class GroupService extends Service{

    private final String SELECT = "SELECT classId, course, semester, number FROM daw.group";

    public GroupService(DataSourceTransactionManager transactionManager) {
        super(transactionManager);
    }

    public List<Group> getAllGroups(String course, String semester, String classId, int offset, int limit){
        return getJdbcTemplate()
                .query(
                        SELECT + " WHERE course = ? and semester = ? and classId = ?" + String.format(PAGINATION, limit, offset),
                        new Object[]{course, semester, classId},
                        new GroupMapper()
                );
    }

    public Group getGroup(String course, String semester, String classId, int number){
        return getJdbcTemplate()
                .queryForObject(
                        SELECT + " WHERE course = ? and semester = ? and classId = ? and number = ?",
                        new Object[]{course, semester, classId, number},
                        new GroupMapper()
                );
    }

    public int postGroup(String course, String semester, String classId, int number){
        return getJdbcTemplate()
                .update("INSERT INTO daw.group(classId, course, semester, number) VALUES (?, ?, ?, ?)",
                        classId, course, semester, number
                );
    }

    public int deleteGroup(String course, String semester, String classId, int number){
        getJdbcTemplate()
                .update("DELETE FROM daw.groupStudent WHERE course = ? and semester = ? and classId = ? and groupNumber = ?",
                        course, semester, classId, number);

        return getJdbcTemplate()
                .update("DELETE FROM daw.group WHERE course = ? and semester = ? and classId = ? and number = ?",
                        course, semester, classId, number);

    }

    public int addStudent(String course, String semester, String classId, int groupNumber, int studentNumber){
        return getJdbcTemplate()
                .update("INSERT INTO daw.groupStudent(classId, course, semester, groupNumber, studentNumber) VALUES (?,?,?,?,?)",
                        classId, course, semester, groupNumber, studentNumber);
    }


    public int deleteStudentFromGroup(String course, String semester, String classId, int groupNumber, int studentNumber) {
        return getJdbcTemplate().update("Delete from daw.groupStudent where classId = ? AND semester = ? AND groupNumber = ? AND studentNumber = ?",
                classId, semester, groupNumber, studentNumber);
    }
    public List<Student> getStudents(String course, String semester, String classId, int number){
        return getJdbcTemplate()
                .query("SELECT daw.user.name, daw.student.number, daw.student.email, daw.user.id, daw.user.password " +
                        "FROM daw.student INNER JOIN daw.groupStudent ON daw.student.number = studentNumber " +
                        "INNER JOIN daw.user ON daw.user.email = daw.student.email " +
                        "WHERE course = ? and semester = ? and classId = ? and groupNumber = ?",
                        new Object[]{course, semester, classId, number},
                        new StudentMapper()
                );
    }

    public int countGroups(String course, String semester, String classId) {
        return getJdbcTemplate()
                .queryForObject(
                        "SELECT count(*) FROM daw.group WHERE course = ? and semester = ? and classId = ?",
                        new Object[]{course, semester, classId},
                        Integer.class
                );
    }
}
