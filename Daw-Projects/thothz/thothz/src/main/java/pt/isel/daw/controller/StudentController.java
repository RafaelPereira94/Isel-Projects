package pt.isel.daw.controller;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.Action;
import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.error.Siren4JException;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.daw.model.Student;
import pt.isel.daw.outputModel.collections.ClassCollectionOutputModel;
import pt.isel.daw.outputModel.collections.StudentCollectionOutputModel;
import pt.isel.daw.outputModel.singles.ClassOutputModel;
import pt.isel.daw.outputModel.singles.StudentOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.StudentService;
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
@RequestMapping(value = "/students", produces = Siren4J.JSON_MEDIATYPE)
public class StudentController extends Controller{

    private final StudentService service;

    public StudentController(StudentService studentService, TeacherService teacherService, ClassService classService){
        super(teacherService, classService);
        service = studentService;
    }

    @GetMapping(name = "students")
    public ResponseEntity<Entity> getStudents(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);

        StudentCollectionOutputModel m = new StudentCollectionOutputModel(
                service.getAllStudents(offset, limit)
                        .stream()
                        .map(s -> {
                            StudentOutputModel st = new StudentOutputModel(s.getNumber(), s.getName(), s.getEmail());
                            st.setEntityLinks(Collections.singletonList(
                                    createLink(Link.RELATIONSHIP_SELF, String.format(path + "/%s", s.getNumber()))));
                            return st;
                        }).collect(Collectors.toCollection(CollectionResource::new)));

        if(authenticatedUserIsAdmin(request)){
            List<Field> fields = new ArrayList<>();
            fields.add(createField("num", NUMBER));
            fields.add(createField("name", TEXT));
            fields.add(createField("email", EMAIL));
            fields.add(createField("password", PASSWORD));
            Action action = createAction(path, "create-student", fields, POST);
            m.setEntityActions(Collections.singletonList(action));
        }

        List<Link> links = createPagingLinks(m, offset, limit, service.countStudents(), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "student", value = "/{id}")
    public ResponseEntity<Entity> getStudents(@PathVariable int id, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request);
        Student std = service.getStudent(id);

        CollectionResource<ClassOutputModel> cl = service.getClasses(id)
                .stream()
                .map(c -> new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(),c.getMax()))
                .collect(Collectors.toCollection(CollectionResource::new));
        setSubEntityUri(cl, path + "/classes");

        StudentOutputModel m = new StudentOutputModel(std.getNumber(), std.getName(), std.getEmail(), cl);

        if(authenticatedUserIsAdmin(request)) {
            m.setEntityActions(Collections.singletonList(createAction(path, "delete-student", DELETE)));
        }

        m.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @PostMapping
    public ResponseEntity postStudent(@RequestParam(value = "num") int num,
                           @RequestParam(value = "name") String name,
                           @RequestParam(value = "email") String email,
                            @RequestParam(value = "password") String password,
                            HttpServletRequest request){
        String path = getPath(request);
        int rows = service.createStudent(num, name, email,password);
        return (rows == 1) ? created(URI.create(path)).build() : badRequest().build();
    }

    @DeleteMapping("/{number}")
    public ResponseEntity deleteStudent(@PathVariable int number){
        int rows = service.deleteStudent(number);
        return rows == 1 ? noContent().build() : notFound().build();
    }

    @GetMapping(name = "student_classes", value = "/{number}/classes")
    public ResponseEntity<Entity> getClasses(@PathVariable int number, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request), classPath = path.split("/students")[0] + "/courses/%s/semesters/%s/classes/%s";
        CollectionResource<ClassOutputModel> l = service.getClasses(number)
                .stream()
                .map(c -> {
                    ClassOutputModel cm = new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax(),
                        getAllTeachers(c.getCourse(), c.getSemester(), c.getId()),
                        getAllStudents(c.getCourse(), c.getSemester(), c.getId()),
                        getAllGroups(c.getCourse(), c.getSemester(), c.getId()));
                    String p = String.format(classPath,
                            c.getCourse(), c.getSemester(), c.getId());
                    cm.setEntityLinks(
                            Collections.singletonList(createLink(RELATIONSHIP_SELF, p)));
                    setSubEntityUri(cm.getTeachers(), p + "/teachers");
                    setSubEntityUri(cm.getStudents(), p + "/students");
                    setSubEntityUri(cm.getGroups(), p + "/groups");
                    return cm;
                }).collect(Collectors.toCollection(CollectionResource::new));

        ClassCollectionOutputModel m = new ClassCollectionOutputModel(l);

        m.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }
}
