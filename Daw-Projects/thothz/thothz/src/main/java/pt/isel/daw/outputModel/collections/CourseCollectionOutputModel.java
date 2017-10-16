package pt.isel.daw.outputModel.collections;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.CourseOutputModel;

@Siren4JEntity(name = "courses")
public class CourseCollectionOutputModel extends CollectionResource<CourseOutputModel>{
    public CourseCollectionOutputModel(CollectionResource<CourseOutputModel> courses) {
        this.addAll(courses);
    }
}

