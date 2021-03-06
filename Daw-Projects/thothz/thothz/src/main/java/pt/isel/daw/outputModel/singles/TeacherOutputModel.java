package pt.isel.daw.outputModel.singles;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.annotations.Siren4JSubEntity;
import com.google.code.siren4j.resource.BaseResource;
import com.google.code.siren4j.resource.CollectionResource;

//todo: add classes that this teacher coordinates
@Siren4JEntity(name = "teacher")
public class TeacherOutputModel extends BaseResource {
    private int number;
    private String name, email;
    @Siren4JSubEntity(rel = "classes", embeddedLink = true)
    private CollectionResource<ClassOutputModel> classes;

    public TeacherOutputModel(int number, String name, String email, CollectionResource<ClassOutputModel> classes) {
        this.number = number;
        this.name = name;
        this.email = email;
        this.classes = classes;
    }

    public TeacherOutputModel(int number, String name, String email) {
        this.number = number;
        this.name = name;
        this.email = email;
    }

    public CollectionResource<ClassOutputModel> getClasses() {
        return classes;
    }

    public void setClasses(CollectionResource<ClassOutputModel> classes) {
        this.classes = classes;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
