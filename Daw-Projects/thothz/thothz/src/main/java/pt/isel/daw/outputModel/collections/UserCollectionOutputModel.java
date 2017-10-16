package pt.isel.daw.outputModel.collections;


import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.CollectionResource;
import pt.isel.daw.outputModel.singles.UserOutputModel;

import java.util.Collection;

@Siren4JEntity(name = "users")
public class UserCollectionOutputModel extends CollectionResource<UserOutputModel>{

    public UserCollectionOutputModel(Collection<UserOutputModel> users) {
        this.addAll(users);
    }
}
