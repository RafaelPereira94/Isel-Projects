package pt.isel.daw.controller;

import com.google.code.siren4j.Siren4J;
import com.google.code.siren4j.component.Action;
import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.converter.ReflectingConverter;
import com.google.code.siren4j.error.Siren4JException;
import com.google.code.siren4j.meta.FieldType;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isel.daw.model.Class;
import pt.isel.daw.model.Group;
import pt.isel.daw.model.Teacher;
import pt.isel.daw.outputModel.collections.GroupCollectionOutputModel;
import pt.isel.daw.outputModel.collections.StudentCollectionOutputModel;
import pt.isel.daw.outputModel.singles.GroupOutputModel;
import pt.isel.daw.outputModel.singles.StudentOutputModel;
import pt.isel.daw.service.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.code.siren4j.component.impl.ActionImpl.Method.DELETE;
import static com.google.code.siren4j.component.impl.ActionImpl.Method.POST;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/courses/{course}/semesters/{semester}/classes/{class}/groups", produces = Siren4J.JSON_MEDIATYPE)
public class GroupController extends Controller{

    private final GroupService groupService;
    private final ClassService classService;
    private final CourseService courseService;
    private final StudentService studentService;

    public GroupController(GroupService groupService, ClassService classService, TeacherService teacherService, CourseService courseService, StudentService studentService) {
        super(teacherService, classService);
        this.groupService = groupService;
        this.classService = classService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @GetMapping(name = "class_groups")
    public ResponseEntity<Entity> getAllGroups(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);

        CollectionResource<GroupOutputModel> col =
                groupService.getAllGroups(course, semester, classId, offset, limit)
                .stream()
                .map(c -> {
                    GroupOutputModel gr = new GroupOutputModel(c.getClassId(), c.getCourse(), c.getSemester(), c.getNumber(),
                            groupService.getStudents(c.getCourse(), c.getSemester(), c.getClassId(), c.getNumber())
                            .stream()
                            .map(s -> new StudentOutputModel( s.getNumber(), s.getName(), s.getEmail()))
                            .collect(Collectors.toCollection(CollectionResource::new))
                    );
                    setSubEntityUri(gr.getStudents(), String.format(path + "/%s/students", c.getNumber()));
                    gr.setEntityLinks(
                            Collections.singletonList(createLink(Link.RELATIONSHIP_SELF, String.format(path + "/%s", c.getNumber()))));
                    return gr;
                })
                .collect(Collectors.toCollection(CollectionResource::new));

        GroupCollectionOutputModel m = new GroupCollectionOutputModel(semester, course, classId, col);

        Teacher t = teacherService.getTeacher(getAuthenticatedUserEmail(request));
        if(t != null){
            if(authenticatedUserIsAdmin(request) ||
                    courseService.isCourseCoordinator(course, t.getNumber()) ||
                    classService.isClassTeacher(course, semester, classId, t.getNumber())){
                m.setEntityActions(Collections.singletonList(createAction(
                        path,
                        "add-group",
                        Collections.singletonList(createField("number", FieldType.NUMBER)),
                        POST
                )));
            }
        }

        List<Link> links = createPagingLinks(m, offset, limit, groupService.countGroups(course, semester, classId), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "class_group", value = "/{number}")
    public ResponseEntity<Entity> getGroup(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @PathVariable int number,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);

        Group g = groupService.getGroup(course, semester, classId, number);
        CollectionResource<StudentOutputModel> s = groupService.getStudents(g.getCourse(), g.getSemester(), g.getClassId(), g.getNumber())
                .stream()
                .map(gr -> new StudentOutputModel(gr.getNumber(), gr.getName(), gr.getEmail()))
                .collect(Collectors.toCollection(CollectionResource::new));

        GroupOutputModel m = new GroupOutputModel(g.getClassId(), g.getCourse(), g.getSemester(), g.getNumber(), s);

        Teacher t = teacherService.getTeacher(getAuthenticatedUserEmail(request));
        if(t != null){
            if(authenticatedUserIsAdmin(request) && m.students.isEmpty() /*||
                        courseService.isCourseCoordinator(course, t.getNumber()) ||
                        classService.isClassTeacher(course, semester, classId, t.getNumber())*/){
                m.setEntityActions(Collections.singletonList(createAction(path, "delete-group", DELETE)));
            }
        }

        setSubEntityUri(s, path + "/students");
        m.setEntityLinks(createBaseLinks(request, path));
        return ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @PostMapping
    public ResponseEntity postGroup(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @RequestParam int number,
            HttpServletRequest request){

        int rows = groupService.postGroup(course, semester, classId, number);
        String path = getPath(request) + "/%s";
        return rows == 1 ? created(URI.create(String.format(path, number))).build() : badRequest().build();
    }


    @DeleteMapping("/{number}")
    public ResponseEntity deleteGroup(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @PathVariable int number){

        int rows = groupService.deleteGroup(course, semester, classId, number);
        return rows == 1 ? noContent().build() : notFound().build();
    }

    @PostMapping("/{groupNumber}/students")
    public ResponseEntity addStudent(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @PathVariable int groupNumber,
            @RequestParam int studentNumber,
            HttpServletRequest request){

        int rows = groupService.addStudent(course, semester, classId, groupNumber, studentNumber);
        String path = getPath(request) + "/%s/students";
        return rows == 1 ? created(URI.create(String.format(path, groupNumber, studentNumber))).build() : badRequest().build();
    }

    @DeleteMapping("/{groupNumber}/students/{studentNumber}")
    public ResponseEntity deleteStudentFromGroup(
        @PathVariable String course,
        @PathVariable String semester,
        @PathVariable ("class") String classId,
        @PathVariable int groupNumber,
        @PathVariable int studentNumber) {

        int rows = groupService.deleteStudentFromGroup(course, semester, classId, groupNumber, studentNumber);
        return rows == 1 ? noContent().build() : notFound().build();
    }

    @GetMapping(name = "class_group_students", value = "/{number}/students")
    public ResponseEntity getStudents(
            @PathVariable String course,
            @PathVariable String semester,
            @PathVariable("class") String classId,
            @PathVariable int number,
            HttpServletRequest request
    ) throws Siren4JException {

        String path = getPath(request);
        String stPath = path.split("/courses")[0].concat("/students/%s");

        Group g = groupService.getGroup(course, semester, classId, number);
        StudentCollectionOutputModel s = new StudentCollectionOutputModel(
                groupService.getStudents(g.getCourse(), g.getSemester(), g.getClassId(), g.getNumber())
                .stream()
                .map(st -> {
                    StudentOutputModel stm = new StudentOutputModel( st.getNumber(), st.getName(), st.getEmail());
                    stm.setEntityLinks(
                            Collections.singletonList(createLink(Link.RELATIONSHIP_SELF, String.format(stPath, st.getNumber()))));
                    stm.setEntityActions(Collections.singletonList(
                            createAction(String.format(path + "/%s", st.getNumber()), "delete-student", DELETE)));
                    return stm;
                }).collect(Collectors.toCollection(CollectionResource::new)));

        Class c = classService.getClass(course, semester, classId);
        if(s.size() < c.getMax()) {
            Action action = createAction(
                    path,
                    "add-student",
                    Collections.singletonList(createField("studentName", FieldType.NUMBER)),
                    POST
            );
            s.setEntityActions(Collections.singletonList(action));
        }

        s.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(s));
    }
}
