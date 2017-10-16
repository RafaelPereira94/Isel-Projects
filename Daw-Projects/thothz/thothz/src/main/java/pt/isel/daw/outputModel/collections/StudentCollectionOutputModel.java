package pt.isel.daw.outputModel.collections;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.StudentOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "students")
public class StudentCollectionOutputModel extends CollectionResource<StudentOutputModel>{
    public StudentCollectionOutputModel(Collection<StudentOutputModel> students) {
        this.addAll(students);
    }
}
