package pt.isel.daw.outputModel.singles;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JSubEntity;
import com.google.code.siren4j.resource.BaseResource;
import com.google.code.siren4j.resource.CollectionResource;

@Siren4JEntity(name = "course")
public class CourseOutputModel extends BaseResource{
    private String name, acronym;
    private int coordinator;
    @Siren4JSubEntity(rel = "classes", embeddedLink = true)
    private CollectionResource<ClassOutputModel> classes;

    public CourseOutputModel(String name, String acronym, int coordinator, CollectionResource<ClassOutputModel> classes) {
        this.name = name;
        this.acronym = acronym;
        this.coordinator = coordinator;
        this.classes = classes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public int getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(int coordinator) {
        this.coordinator = coordinator;
    }

    public CollectionResource<ClassOutputModel> getClasses() {
        return classes;
    }

    public void setClasses(CollectionResource<ClassOutputModel> classes) {
        this.classes = classes;
    }
}


