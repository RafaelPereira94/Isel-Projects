package pt.isel.daw.outputModel.collections;


import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JPropertyIgnore;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.ClassOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "classes")
public class ClassCollectionOutputModel extends CollectionResource<ClassOutputModel> {
    @Siren4JPropertyIgnore
    private String semester;

    @Siren4JPropertyIgnore
    private String course;

    public ClassCollectionOutputModel(String semester, String course, Collection<ClassOutputModel> classes) {
        this.semester = semester;
        this.course = course;
        this.addAll(classes);
    }

    public ClassCollectionOutputModel(String course, Collection<ClassOutputModel> classes) {
        this.course = course;
        this.addAll(classes);
    }

    public ClassCollectionOutputModel(Collection<ClassOutputModel> classes) {
        this.addAll(classes);
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
}
