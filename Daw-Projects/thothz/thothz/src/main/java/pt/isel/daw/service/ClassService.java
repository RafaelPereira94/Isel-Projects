package pt.isel.daw.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import pt.isel.daw.model.Class;
import pt.isel.daw.model.Group;
import pt.isel.daw.model.Student;
import pt.isel.daw.model.Teacher;
import pt.isel.daw.rowMapper.ClassMapper;
import pt.isel.daw.rowMapper.GroupMapper;
import pt.isel.daw.rowMapper.StudentMapper;
import pt.isel.daw.rowMapper.TeacherMapper;

import java.util.List;

@org.springframework.stereotype.Service
public class ClassService extends Service {

    private final String SELECT = "SELECT course, semester, id, max FROM daw.class";

    public ClassService(DataSourceTransactionManager transactionManager) {
        super(transactionManager);
    }

    public List<Class> getAllClasses(String course, String semester, int offset, int limit){
        return getJdbcTemplate()
                .query(
                        SELECT + " WHERE course = ? and semester = ?" + String.format(PAGINATION, limit, offset),
                        new Object[]{course, semester},
                        new ClassMapper()
                );
    }

    public List<Class> getCourseClasses(String name, int offset, int limit) {
        String query = "SELECT DISTINCT course,semester,id,max FROM daw.course JOIN daw.class ON(name = course) WHERE name = ?"
                + String.format(PAGINATION, limit, offset);
        return getJdbcTemplate().query(query,new Object[]{name},new ClassMapper());
    }

    public Class getClass(String course, String semester, String id){
        return getJdbcTemplate()
                .queryForObject(
                        SELECT + " WHERE course = ? and semester = ? and id = ?",
                        new Object[]{course, semester, id},
                        new ClassMapper()
                );
    }

    public int postClass(String course, String semester, String id, int max){
        return getJdbcTemplate()
                .update(
                        "INSERT INTO daw.class(course, semester, id, max) VALUES (?, ?, ?, ?)",
                        course, semester, id, max
                );
    }

    public int deleteClass(String course, String semester, String id){
        return getJdbcTemplate()
                .update("DELETE FROM daw.class WHERE course = ? and semester = ? and id = ?", course, semester, id);
    }

    public int addStudent(String course, String semester, String id, int studentNumber){
        return getJdbcTemplate()
                .update("INSERT INTO daw.classStudent(classId, course, semester, studentNumber) VALUES (?,?,?,?)",
                        id, course, semester, studentNumber);
    }

    public int addTeacher(String course, String semester, String id, int teacherNumber) {
        return getJdbcTemplate()
                .update("INSERT INTO daw.classTeacher(classId, course, semester, teacherNumber) VALUES (?,?,?,?)",
                        id, course, semester, teacherNumber);
    }

    public List<Teacher> getTeachers(String course, String semester, String id) {
        return getJdbcTemplate()
                .query("SELECT daw.user.name, daw.teacher.number, daw.teacher.email, daw.teacher.admin, daw.user.id, daw.user.password " +
                        "FROM daw.teacher INNER JOIN daw.classTeacher ON daw.teacher.number = teacherNumber " +
                        "INNER JOIN daw.user ON daw.user.email = daw.teacher.email " +
                        "WHERE course = ? and semester = ? and classId = ?",
                        new Object[]{course, semester, id},
                        new TeacherMapper()
                );
    }

    public List<Student> getStudents(String course, String semester, String id) {
        return getJdbcTemplate()
                .query("SELECT daw.user.name, daw.student.number, daw.student.email, daw.user.id, daw.user.password " +
                                "FROM daw.student INNER JOIN daw.classStudent ON daw.student.number = studentNumber " +
                                "INNER JOIN daw.user ON daw.user.email = daw.student.email " +
                                "WHERE course = ? and semester = ? and classId = ?",
                        new Object[]{course, semester, id},
                        new StudentMapper()
                );
    }

    public List<Group> getGroups(String course, String semester, String id) {
        return getJdbcTemplate()
                .query("SELECT daw.group.classId, daw.group.course, daw.group.semester, daw.group.number " +
                                "FROM daw.group INNER JOIN daw.groupStudent ON daw.group.classId = daw.groupStudent.classId and " +
                                "daw.group.course = daw.groupStudent.course and daw.group.semester = daw.groupStudent.semester " +
                                "WHERE daw.group.course = ? and daw.group.semester = ? and daw.group.classId = ?",
                        new Object[]{course, semester, id},
                        new GroupMapper()
                );
    }

    public int deleteTeacher(String course, String semester, String id, int teacherNum) {
        return getJdbcTemplate().update("DELETE FROM daw.classTeacher WHERE classId = ? AND course = ? AND semester = ? AND teacherNumber = ?",
                id,course,semester,teacherNum);
    }

    public int deleteStudent(String course, String semester, String id, int studentNum) {
        return getJdbcTemplate().update("DELETE FROM daw.classStudent WHERE classId = ? AND course = ? AND semester = ? AND studentNumber = ?",
                id,course,semester,studentNum);
    }

    public int countClasses(String course, String semester) {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.class WHERE course = ? and semester = ?",
                        new Object[]{course, semester},
                        Integer.class);
    }

    public int countClasses(String course) {
        return getJdbcTemplate()
                .queryForObject("SELECT count(*) FROM daw.class WHERE course = ?",
                        new Object[]{course},
                        Integer.class);
    }

    public boolean isClassTeacher(String course, String semester, String classId, int teacherNumber){
        return getTeachers(course, semester, classId).stream().anyMatch(t -> t.getNumber() == teacherNumber);
    }
}
