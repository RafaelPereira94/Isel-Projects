package pt.isel.daw.controller;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.error.Siren4JException;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.daw.model.User;
import pt.isel.daw.outputModel.collections.UserCollectionOutputModel;
import pt.isel.daw.outputModel.singles.UserOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.TeacherService;
import pt.isel.daw.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users", produces = Siren4J.JSON_MEDIATYPE)
public class UserController extends Controller{

    private final UserService userService;

    public UserController(UserService studentService, TeacherService teacherService, ClassService classService) {
        super(teacherService, classService);
        this.userService = studentService;
    }

    @GetMapping(name = "users")
    public ResponseEntity<Entity> getAllUsers(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {
        String path = getPath(request);

        CollectionResource<UserOutputModel> users =
                userService.getAllUsers(offset, limit)
                .stream()
                .map(u -> {
                    UserOutputModel m = new UserOutputModel(u.getName(), u.getEmail(), u.getId());
                    m.setEntityLinks(Collections.singletonList(
                            createLink(Link.RELATIONSHIP_SELF, String.format(path + "/%s", u.getEmail()))));
                    return m;
                }).collect(Collectors.toCollection(CollectionResource::new));

        UserCollectionOutputModel m = new UserCollectionOutputModel(users);

        List<Link> links = createPagingLinks(m, offset, limit, userService.countUsers(), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "user", value = "/{email}")
    public ResponseEntity<Entity> getUser(@PathVariable String email, HttpServletRequest request) throws Siren4JException {
        User user = userService.getUser(email);
        UserOutputModel m = new UserOutputModel(user.getName(), user.getEmail(), user.getId());
        m.setEntityLinks(createBaseLinks(request, getPath(request)));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }
}