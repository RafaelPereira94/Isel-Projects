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
import pt.isel.daw.model.Course;
import pt.isel.daw.outputModel.collections.ClassCollectionOutputModel;
import pt.isel.daw.outputModel.collections.CourseCollectionOutputModel;
import pt.isel.daw.outputModel.singles.ClassOutputModel;
import pt.isel.daw.outputModel.singles.CourseOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.CourseService;
import pt.isel.daw.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.code.siren4j.component.impl.ActionImpl.Method.DELETE;
import static com.google.code.siren4j.component.impl.ActionImpl.Method.POST;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/courses", produces = Siren4J.JSON_MEDIATYPE)
public class CourseController extends Controller{

    private final CourseService service;
    private final ClassService classService;

    public CourseController(CourseService service, TeacherService teacherService, ClassService classService) {
        super(teacherService, classService);
        this.classService = classService;
        this.service = service;
    }

    @GetMapping(name = "courses")
    public ResponseEntity<Entity> getAllCourses(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "q", required = false) String q,
            HttpServletRequest request
    ) throws Siren4JException {
        String path = getPath(request);


        CollectionResource<CourseOutputModel> c = (q == null) ? getCourses(offset, limit) : getCourses(q,offset,limit);

        c.forEach(course -> {
            course.setEntityLinks(
                    Collections.singletonList(createLink(Link.RELATIONSHIP_SELF, String.format(path + "/%s", course.getName()))));
            setSubEntityUri(course.getClasses(), String.format(path + "/%s/semesters/all/classes", course.getName()));
        });

        CourseCollectionOutputModel m = new CourseCollectionOutputModel(c);

        if(authenticatedUserIsAdmin(request)){
            List<Field> fields = new ArrayList<>();
            fields.add(createField("name", FieldType.TEXT));
            fields.add(createField("acronym", FieldType.TEXT));
            fields.add(createField("coordinator", FieldType.NUMBER));
            m.setEntityActions(Collections.singletonList(createAction(path, "create-course", fields, POST)));
        }

        List<Link> links = createPagingLinks(m, offset, limit, service.countCourses(), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    private CollectionResource<CourseOutputModel> getCourses(int offset, int limit) {
        return service.getAllCourses(offset, limit)
                .stream()
                .map(c -> new CourseOutputModel(c.getName(), c.getAcronym(), c.getCoordinator(), getCourseClasses(c.getName())))
                .collect(Collectors.toCollection(CollectionResource::new));
    }

    private CollectionResource<CourseOutputModel> getCourses(String name, int offset, int limit) {
        return service.getCoursesByName(name,offset, limit)
                .stream()
                .map(c -> new CourseOutputModel(c.getName(), c.getAcronym(), c.getCoordinator(), getCourseClasses(c.getName())))
                .collect(Collectors.toCollection(CollectionResource::new));
    }

    @GetMapping(name = "course", value = "/{name}")
    public ResponseEntity<Entity> getCourse(@PathVariable String name, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request);

        CollectionResource<ClassOutputModel> classes = getCourseClasses(name);

        Course cs = service.getCourse(name);
        CourseOutputModel m = new CourseOutputModel(cs.getName(), cs.getAcronym(), cs.getCoordinator(), classes);

        if(authenticatedUserIsAdmin(request) && classes.isEmpty()){
            m.setEntityActions(Collections.singletonList(createAction(path, "delete-course", DELETE)));
        }

        setSubEntityUri(classes, path + "/semesters/all/classes");
        m.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    private CollectionResource<ClassOutputModel> getCourseClasses(String name) {
        return new ClassCollectionOutputModel(
                    name,
                    classService.getCourseClasses(name, 0, 20)
                        .stream()
                        .map(c -> new ClassOutputModel(c.getId(), c.getSemester(), c.getCourse(), c.getMax()))
                        .collect(Collectors.toCollection(CollectionResource::new))
            );
    }

    @PostMapping
    public ResponseEntity insertCourse(@RequestParam(value = "name") String name,
                            @RequestParam(value = "acronym")String acronym,
                            @RequestParam(value = "coordinator")int coordinator,
                           HttpServletRequest request){

        int rows = service.createCourse(name, acronym, coordinator);
        String path = getPath(request) + "/%s";
        return rows == 1 ? created(URI.create(String.format(path, name))).build() : badRequest().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteCourse(@PathVariable("name") String name){
        int rows = service.deleteCourse(name);
        return rows == 1 ? noContent().build() : notFound().build();
    }

}
