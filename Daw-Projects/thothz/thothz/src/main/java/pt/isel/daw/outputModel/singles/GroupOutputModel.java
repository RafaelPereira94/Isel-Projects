package pt.isel.daw.outputModel.singles;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JSubEntity;
import com.google.code.siren4j.resource.BaseResource;
import com.google.code.siren4j.resource.CollectionResource;

@Siren4JEntity(name = "group")
public class GroupOutputModel extends BaseResource {
    private String classId, course, semester;
    private int number;

    public GroupOutputModel(String classId, String course, String semester, int number, CollectionResource<StudentOutputModel> students) {
        this.classId = classId;
        this.course = course;
        this.semester = semester;
        this.number = number;
        this.students = students;
    }

    @Siren4JSubEntity(rel = "students", embeddedLink = true)
    public CollectionResource<StudentOutputModel> students;

    public GroupOutputModel(String classId, String course, String semester, int number) {
        this.classId = classId;
        this.course = course;
        this.semester = semester;
        this.number = number;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public CollectionResource<StudentOutputModel> getStudents() {
        return students;
    }

    public void setStudents(CollectionResource<StudentOutputModel> students) {
        this.students = students;
    }
}
