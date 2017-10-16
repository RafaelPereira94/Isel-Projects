package pt.isel.daw.controller;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.error.Siren4JException;
import com.google.code.siren4j.meta.FieldType;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.daw.model.Class;
import pt.isel.daw.model.Teacher;
import pt.isel.daw.outputModel.collections.ClassCollectionOutputModel;
import pt.isel.daw.outputModel.collections.StudentCollectionOutputModel;
import pt.isel.daw.outputModel.singles.ClassOutputModel;
import pt.isel.daw.outputModel.singles.GroupOutputModel;
import pt.isel.daw.outputModel.singles.StudentOutputModel;
import pt.isel.daw.outputModel.singles.TeacherOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.CourseService;
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
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/courses/{course}/semesters/{semester}/classes", produces = Siren4J.JSON_MEDIATYPE)
public class ClassController extends Controller{

    private final ClassService service;
    private final CourseService courseService;

    public ClassController(ClassService service, TeacherService teacherService, CourseService courseService, ClassService classService) {
        super(teacherService, classService);
        this.service = service;
        this.courseService = courseService;
    }

    @GetMapping(name = "course_classes")
    public ResponseEntity<Entity> getAllClasses(
            @PathVariable String course,
            @PathVariable String semester,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);
        CollectionResource<ClassOutputModel> classes;
        ClassCollectionOutputModel m;
        List<Link> links;

        if(semester.contentEquals("all")){
            classes = service.getCourseClasses(course, offset, limit)
                    .stream()
                    .map(c -> {
                        ClassOutputModel cm = new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax(),
                                getAllTeachers(course, c.getSemester(), c.getId()),
                                getAllStudents(course, c.getSemester(), c.getId()),
                                getAllGroups(course, c.getSemester(), c.getId()));
                        String p = String.format(path.replace("all", cm.getSemester()) + "/%s", c.getId());
                        cm.setEntityLinks(
                                Collections.singletonList(createLink(RELATIONSHIP_SELF, p)));
                        setSubEntityUri(cm.getTeachers(), p + "/teachers");
                        setSubEntityUri(cm.getStudents(), p + "/students");
                        setSubEntityUri(cm.getGroups(), p + "/groups");
                        return cm;
                    }).collect(Collectors.toCollection(CollectionResource::new));

            m = new ClassCollectionOutputModel(course, classes);
            links = createPagingLinks(m, offset, limit, service.countClasses(course), path);
        }
        else{
            classes = getClasses(course, semester, offset, limit);
            classes.forEach(c -> {
                String p = String.format(path + "/%s", c.getId());
                c.setEntityLinks(Collections.singletonList(createLink(RELATIONSHIP_SELF, p)));
                setSubEntityUri(c.getTeachers(), p);
                setSubEntityUri(c.getStudents(), p);
                setSubEntityUri(c.getGroups(), p);
            });

            m = new ClassCollectionOutputModel(semester, course, classes);
            links = createPagingLinks(m, offset, limit, service.countClasses(course, semester), path);

            Teacher t = teacherService.getTeacher(getAuthenticatedUserEmail(request));
            if(t != null){
                if(authenticatedUserIsAdmin(request) || courseService.isCourseCoordinator(course, t.getNumber())){
                    List<Field> fields = new ArrayList<>();
                    fields.add(createField("id", FieldType.TEXT));
                    fields.add(createField("max", FieldType.NUMBER));
                    m.setEntityActions(Collections.singletonList(createAction(path, "create-class", fields, POST)));
                }
            }
        }

        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ok(ReflectingConverter.newInstance().toEntity(m));
    }

    private CollectionResource<ClassOutputModel> getClasses(
            String course,
            String semester,
            int offset,
            int limit
    ) {
        return service.getAllClasses(course, semester, offset, limit)
            .stream()
            .map(c -> new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax(),
                    getAllTeachers(course, semester, c.getId()),
                    getAllStudents(course, semester, c.getId()),
                    getAllGroups(course, semester, c.getId())))
            .collect(Collectors.toCollection(CollectionResource::new));
    }

    //todo - add action to add or remove teacher, student and group
    @GetMapping(name = "course_class", value = "/{id}")
    public ResponseEntity<Entity> getClass(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            HttpServletRequest request
    ) throws Siren4JException {
        String path = getPath(request);

        CollectionResource<TeacherOutputModel> ts = getAllTeachers(course, semester, id);
        CollectionResource<StudentOutputModel> ss = getAllStudents(course, semester, id);
        CollectionResource<GroupOutputModel> gs = getAllGroups(course, semester, id);

        setSubEntityUri(ts, path + "/teachers");
        setSubEntityUri(ss, path + "/students");
        setSubEntityUri(gs, path + "/groups");

        Class c = service.getClass(course, semester, id);
        ClassOutputModel m = new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax(), ts, ss, gs);

        Teacher t = teacherService.getTeacher(getAuthenticatedUserEmail(request));
        if(t != null){
            if(authenticatedUserIsAdmin(request) || courseService.isCourseCoordinator(course, t.getNumber())) {
                m.setEntityActions(Collections.singletonList(createAction(path, "delete-class", DELETE)));
            }
        }

        m.setEntityLinks(createBaseLinks(request, path));
        return ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @PostMapping
    public ResponseEntity postClass(
            @PathVariable String course,
            @PathVariable String semester,
            @RequestParam String id,
            @RequestParam int max,
            HttpServletRequest request){
        String path = getPath(request) + "/%s";

        int rows = service.postClass(course, semester, id, max);
        return rows == 1 ? created(URI.create(String.format(path, id))).build() : badRequest().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteClass(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id){

        int rows = service.deleteClass(course, semester, id);
        return rows == 1 ? noContent().build() : notFound().build();
    }

    @PostMapping(path = "/{id}/students")
    public ResponseEntity addStudent(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            @RequestParam int studentNumber){

        int rows = service.addStudent(course, semester, id, studentNumber);
        return rows == 1 ? ok().build() : badRequest().build();
    }

    @PostMapping(path = "/{id}/teachers")
    public ResponseEntity addTeacher(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            @RequestParam int teacherNumber){

        int rows = service.addTeacher(course, semester, id, teacherNumber);
        return rows == 1 ? ok().build() : badRequest().build();
    }

    @GetMapping(name = "class_teachers", value = "/{id}/teachers")
    public ResponseEntity<Entity> getTeachers(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);
        String subEntityPath = path.split("courses")[0];
        Teacher at = teacherService.getTeacher(getAuthenticatedUserEmail(request));
        CollectionResource<TeacherOutputModel> m = service.getTeachers(course, semester, id)
                .stream()
                .map(t -> {
                    TeacherOutputModel tc = new TeacherOutputModel(t.getNumber(), t.getName(), t.getEmail(),
                            getClasses(course, semester, 0, 20));
                    if(at != null){
                        if(authenticatedUserIsAdmin(request) ||
                        courseService.isCourseCoordinator(course, at.getNumber())) {
                            tc.setEntityActions(Collections.singletonList(
                                    createAction(String.format(path + "/%d", t.getNumber()), "delete-teacher", DELETE)));
                        }
                    }
                    setSubEntityUri(tc.getClasses(), String.format(subEntityPath + "teachers/%s/classes", tc.getNumber()));
                    tc.setEntityLinks(Collections.singletonList(
                            createLink(Link.RELATIONSHIP_SELF, String.format(subEntityPath + "teachers/%s", tc.getNumber()))));
                    return tc;
                }).collect(Collectors.toCollection(CollectionResource::new));

        if(at != null){
        if(authenticatedUserIsAdmin(request) ||
        courseService.isCourseCoordinator(course, at.getNumber())) {
            m.setEntityActions(Collections.singletonList(createAction(
                    path,
                    "add-teacher",
                    Collections.singletonList(createField("teacherNumber", FieldType.NUMBER)),
                    POST))
            );
        }
        }

        m.setEntityLinks(createBaseLinks(request, path));
        return ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "class_students", value = "/{id}/students")
    public ResponseEntity<Entity> getStudents(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);
        String subEntityPath = path.split("courses")[0];
        Teacher at = teacherService.getTeacher(getAuthenticatedUserEmail(request));
        CollectionResource<StudentOutputModel> m = service.getStudents(course, semester, id)
                .stream()
                .map(s -> {
                    StudentOutputModel st = new StudentOutputModel(s.getNumber(), s.getName(), s.getEmail(),
                            getClasses(course, semester, 0, 20));
                    if(at != null){
                        if(authenticatedUserIsAdmin(request) ||
                        courseService.isCourseCoordinator(course, at.getNumber()) ||
                        classService.isClassTeacher(course, semester, id, at.getNumber())
                        /* || enrolment && isStudent)*/) {
                            st.setEntityActions(Collections.singletonList(
                                    createAction(String.format(path + "/%s", s.getNumber()), "delete-student", DELETE)));
                        }
                    }
                    setSubEntityUri(st.getClasses(), String.format(subEntityPath + "students/%s/classes", st.getNumber()));
                    st.setEntityLinks(Collections.singletonList(
                            createLink(Link.RELATIONSHIP_SELF, String.format(subEntityPath + "students/%s", st.getNumber()))));
                    return st;
                }).collect(Collectors.toCollection(CollectionResource::new));

        StudentCollectionOutputModel l = new StudentCollectionOutputModel(m);

        if(at != null){
            if(authenticatedUserIsAdmin(request) ||
                    courseService.isCourseCoordinator(course, at.getNumber()) ||
                    classService.isClassTeacher(course, semester, id, at.getNumber())
                        /*|| (enrolment && isStudent)*/) {
                l.setEntityActions(Collections.singletonList(createAction(
                        path,
                        "add-student",
                        Collections.singletonList(createField("studentNumber", FieldType.NUMBER)),
                        POST)));
            }
        }

        l.setEntityLinks(createBaseLinks(request, path));
        return ok(ReflectingConverter.newInstance().toEntity(l));
    }

    @DeleteMapping(value = "/{id}/teachers/{teacherNumber}")
    public ResponseEntity deleteTeacher(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            @PathVariable int teacherNum){
        int rows = service.deleteTeacher(course, semester, id, teacherNum);
        return rows == 1 ? noContent().build() : notFound().build();
    }

    @DeleteMapping(value = "/{id}/students/{studentNumber}")
    public ResponseEntity deleteStudent(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable String id,
            @PathVariable int studentNumber){
        int rows = service.deleteStudent(course, semester, id, studentNumber);
        return rows == 1 ? noContent().build() : notFound().build();
    }
}