package pt.isel.daw.controller;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.error.Siren4JException;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.daw.model.Teacher;
import pt.isel.daw.outputModel.collections.ClassCollectionOutputModel;
import pt.isel.daw.outputModel.collections.TeacherCollectionOutputModel;
import pt.isel.daw.outputModel.singles.ClassOutputModel;
import pt.isel.daw.outputModel.singles.TeacherOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.code.siren4j.component.Link.RELATIONSHIP_SELF;
import static com.google.code.siren4j.component.impl.ActionImpl.Method.DELETE;
import static com.google.code.siren4j.component.impl.ActionImpl.Method.POST;
import static com.google.code.siren4j.meta.FieldType.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/teachers", produces = Siren4J.JSON_MEDIATYPE)
public class TeacherController extends Controller{

    private final TeacherService service;

    public TeacherController(TeacherService service, ClassService classService) {
        super(service, classService);
        this.service = service;
    }

    @GetMapping(name = "teachers")
    public ResponseEntity<Entity> getTeachers(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);

        TeacherCollectionOutputModel m = new TeacherCollectionOutputModel(service.getAllTeachers(offset, limit)
                .stream()
                .map(t -> {
                    TeacherOutputModel tc = new TeacherOutputModel(t.getNumber(), t.getName(), t.getEmail());
                    tc.setEntityLinks(Collections.singletonList(
                            createLink(RELATIONSHIP_SELF, String.format(path + "/%s", t.getNumber()))));
                    return tc;
                }).collect(Collectors.toCollection(CollectionResource::new)))
                ;

        if(authenticatedUserIsAdmin(request)) {
            List<Field> fields = new ArrayList<>();
            fields.add(createField("number", NUMBER));
            fields.add(createField("name", TEXT));
            fields.add(createField("email", EMAIL));
            fields.add(createField("password", PASSWORD));
            m.setEntityActions(Collections.singletonList(createAction(path, "create-teacher", fields, POST)));
        }

        List<Link> links = createPagingLinks(m, offset, limit, service.countTeachers(), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "teacher", value = "/{number}")
    public ResponseEntity<Entity> getTeacher(@PathVariable int number, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request);
        Teacher teacher = service.getTeacher(number);

        CollectionResource<ClassOutputModel> l = service.getClasses(number)
                .stream()
                .map(c -> new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax()))
                .collect(Collectors.toCollection(CollectionResource::new));
        setSubEntityUri(l, path + "/classes");

        TeacherOutputModel m = new TeacherOutputModel(teacher.getNumber(), teacher.getName(), teacher.getEmail(), l);

        if(authenticatedUserIsAdmin(request)) {
            m.setEntityActions(Collections.singletonList(createAction(path, "delete-teacher", DELETE)));
        }

        m.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @PostMapping
    public ResponseEntity postStudent(
            @RequestParam(value = "number") int number,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "admin") String admin,
            @RequestParam(value = "password") String password,
            HttpServletRequest request
    ){
        int rows = service.createTeacher(number, name, email, admin,password);
        return rows == 1 ? created(URI.create(getPath(request))).build():badRequest().build();
    }

    @DeleteMapping("/{number}")
    public ResponseEntity deleteStudent(@PathVariable int number) {
        int rows = service.deleteTeacher(number);
        return (rows == 1) ?  noContent().build() : notFound().build();
    }

    @GetMapping(name = "teacher_classes", value = "/{number}/classes")
    public ResponseEntity<Entity> getClasses(@PathVariable int number, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request), classPath = path.split("/teachers")[0] + "/courses/%s/semesters/%s/classes/%s";
        CollectionResource<ClassOutputModel> l = service.getClasses(number)
                .stream()
                .map(c -> {
                    ClassOutputModel cm = new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax(),
                            getAllTeachers(c.getCourse(), c.getSemester(), c.getId()),
                            getAllStudents(c.getCourse(), c.getSemester(), c.getId()),
                            getAllGroups(c.getCourse(), c.getSemester(), c.getId()));

                    String p = String.format(classPath, c.getCourse(), c.getSemester(), c.getId());
                    cm.setEntityLinks(Collections.singletonList(createLink(RELATIONSHIP_SELF, p)));
                    setSubEntityUri(cm.getTeachers(), p + "/teachers");
                    setSubEntityUri(cm.getStudents(), p + "/students");
                    setSubEntityUri(cm.getGroups(), p + "/groups");
                    return cm;
                }).collect(Collectors.toCollection(CollectionResource::new));

        ClassCollectionOutputModel m = new ClassCollectionOutputModel(l);

        m.setEntityLinks(createBaseLinks(request, path));
        return ok(ReflectingConverter.newInstance().toEntity(m));
    }
}
