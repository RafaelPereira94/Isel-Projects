package pt.isel.daw.outputModel.collections;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.SemesterOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "semesters")
public class SemesterCollectionOutputModel extends CollectionResource<SemesterOutputModel>{

    public SemesterCollectionOutputModel(Collection<SemesterOutputModel> semester) {
        this.addAll(semester);
    }
}
