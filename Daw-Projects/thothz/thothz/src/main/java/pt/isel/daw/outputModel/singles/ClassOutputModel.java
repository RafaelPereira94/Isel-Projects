package pt.isel.daw.outputModel.singles;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JSubEntity;
import com.google.code.siren4j.resource.BaseResource;
import com.google.code.siren4j.resource.CollectionResource;

@Siren4JEntity(name = "class")
public class ClassOutputModel extends BaseResource{
        private String id, semester, course;
        private int max;

        @Siren4JSubEntity(rel = "teachers", embeddedLink = true)
        private CollectionResource<TeacherOutputModel> teachers;

        @Siren4JSubEntity(rel = "students", embeddedLink = true)
        private CollectionResource<StudentOutputModel> students;

        @Siren4JSubEntity(rel = "groups", embeddedLink = true)
        private CollectionResource<GroupOutputModel> groups;

        public ClassOutputModel(
                String id,
                String semester,
                String course,
                int max,
                CollectionResource<TeacherOutputModel> teachers,
                CollectionResource<StudentOutputModel> students,
                CollectionResource<GroupOutputModel> groups
        ) {
                this.id = id;
                this.semester = semester;
                this.course = course;
                this.max = max;
                this.teachers = teachers;
                this.students = students;
                this.groups = groups;
        }

    public ClassOutputModel(String id, String semester, String course, int max) {
        this.id = id;
        this.semester = semester;
        this.course = course;
        this.max = max;
    }

    public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getSemester() {
                return semester;
        }

        public void setSemester(String semester) {
                this.semester = semester;
        }

        public String getCourse() {
                return course;
        }

        public void setCourse(String course) {
                this.course = course;
        }

        public int getMax() {
                return max;
        }

        public void setMax(int max) {
                this.max = max;
        }

        public CollectionResource<TeacherOutputModel> getTeachers() {
                return teachers;
        }

        public void setTeachers(CollectionResource<TeacherOutputModel> teachers) {
                this.teachers = teachers;
        }

        public CollectionResource<StudentOutputModel> getStudents() {
                return students;
        }

        public void setStudents(CollectionResource<StudentOutputModel> students) {
                this.students = students;
        }

        public CollectionResource<GroupOutputModel> getGroups() {
                return groups;
        }

        public void setGroups(CollectionResource<GroupOutputModel> groups) {
                this.groups = groups;
        }

        public boolean condition(){
                if(students == null || !students.isEmpty())
                        return false;
                if(teachers == null || !teachers.isEmpty())
                        return false;
                if(groups == null || !groups.isEmpty())
                        return false;
                return true;
        }
}