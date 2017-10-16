package pt.isel.daw.outputModel.collections;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JPropertyIgnore;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.GroupOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "groups")
public class GroupCollectionOutputModel extends CollectionResource<GroupOutputModel> {
    @Siren4JPropertyIgnore
    private String semester;
    @Siren4JPropertyIgnore
    private String course;
    @Siren4JPropertyIgnore
    private String classId;

    public GroupCollectionOutputModel(String semester, String course, String classId, Collection<GroupOutputModel> groups) {
        this.semester = semester;
        this.course = course;
        this.classId = classId;
        this.addAll(groups);
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
