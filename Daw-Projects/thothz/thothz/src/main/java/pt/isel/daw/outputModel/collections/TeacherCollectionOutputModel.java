package pt.isel.daw.outputModel.collections;


import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.TeacherOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "teachers")
public class TeacherCollectionOutputModel extends CollectionResource<TeacherOutputModel> {

    public TeacherCollectionOutputModel(Collection<TeacherOutputModel> teachers) {
        this.addAll(teachers);
    }

}
