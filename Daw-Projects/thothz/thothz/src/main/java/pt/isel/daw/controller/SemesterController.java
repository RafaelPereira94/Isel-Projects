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
import pt.isel.daw.model.Semester;
import pt.isel.daw.outputModel.collections.SemesterCollectionOutputModel;
import pt.isel.daw.outputModel.singles.SemesterOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.SemesterService;
import pt.isel.daw.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.code.siren4j.component.impl.ActionImpl.Method.DELETE;
import static com.google.code.siren4j.component.impl.ActionImpl.Method.POST;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(value = "/semesters", produces = Siren4J.JSON_MEDIATYPE)
public class SemesterController extends Controller{

    private final SemesterService service;

    public SemesterController(SemesterService service, TeacherService teacherService, ClassService classService) {
        super(teacherService, classService);
        this.service = service;
    }

    @GetMapping(name = "semesters")
    public ResponseEntity<Entity> getAllSemesters(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            HttpServletRequest request
    ) throws Siren4JException {
        String path = getPath(request);
        Collection<SemesterOutputModel> col = service.getAllSemesters(offset, limit)
                .stream()
                .map(s -> {
                    SemesterOutputModel st = new SemesterOutputModel(s.getName(), s.getSeason(), s.getYear());
                    st.setEntityLinks(
                            Collections.singletonList(createLink(Link.RELATIONSHIP_SELF, String.format(path + "/%s", s.getName()))));
                    return st;
                }).collect(Collectors.toCollection(CollectionResource::new));

        SemesterCollectionOutputModel m = new SemesterCollectionOutputModel(col);

        if(authenticatedUserIsAdmin(request)){
            List<Field> fields = new ArrayList<>();
            fields.add(createField("year", FieldType.NUMBER));
            fields.add(createField("name", FieldType.TEXT));
            fields.add(createField("season", FieldType.TEXT));
            m.setEntityActions(Collections.singletonList(createAction(path, "create-semester", fields, POST)));
        }

        List<Link> links = createPagingLinks(m, offset, limit, service.countSemesters(), path);
        links.addAll(createBaseLinks(request, path));
        m.setEntityLinks(links);
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }

    @GetMapping(name = "semester", value = "/{name}")
    public ResponseEntity<Entity> getSemester(@PathVariable String name, HttpServletRequest request) throws Siren4JException {
        String path = getPath(request);
        Semester sm = service.getSemester(name);
        SemesterOutputModel m = new SemesterOutputModel(sm.getName(), sm.getSeason(), sm.getYear());

        if(authenticatedUserIsAdmin(request)) {
            m.setEntityActions(Collections.singletonList(createAction(path, "delete-semester", DELETE)));
        }

        m.setEntityLinks(createBaseLinks(request, path));
        return ResponseEntity.ok(ReflectingConverter.newInstance().toEntity(m));
    }


    @PostMapping
    public ResponseEntity postSemester(
            @RequestParam String name,
            @RequestParam int year,
            @RequestParam String season,
            HttpServletRequest request){
        int rows = service.postSemester(name, year, season);
        return rows == 1 ? created(URI.create(String.format(getPath(request) + "/%s", name))).build() : badRequest().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteSemester(@PathVariable String name){
        int rows = service.deleteSemester(name);
        return rows == 1 ? noContent().build() : notFound().build();
    }
}
