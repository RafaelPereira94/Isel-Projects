package pt.isel.daw.controller;

import com.google.code.siren4j.component.Action;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.component.builder.ActionBuilder;
import com.google.code.siren4j.component.builder.FieldBuilder;
import com.google.code.siren4j.component.builder.LinkBuilder;
import com.google.code.siren4j.meta.FieldType;
import com.google.code.siren4j.resource.BaseResource;
import com.google.code.siren4j.resource.CollectionResource;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.daw.outputModel.singles.GroupOutputModel;
import pt.isel.daw.outputModel.singles.StudentOutputModel;
import pt.isel.daw.outputModel.singles.TeacherOutputModel;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.code.siren4j.component.impl.ActionImpl.Method;

@RestController
class Controller {

    final TeacherService teacherService;
    final ClassService classService;

    Controller(TeacherService teacherService, ClassService classService){
        this.teacherService = teacherService;
        this.classService = classService;
    }

    List<Link> createPagingLinks(CollectionResource resource, int offset, int limit, long total, String path){
        path += "?limit=%s&offset=%s";
        resource.setLimit(limit);
        resource.setOffset(offset);
        resource.setTotal(total);
        List<Link> links = new ArrayList<>();
        if(offset + 1 < Math.ceil(total / limit))
            links.add(createLink(Link.RELATIONSHIP_NEXT, String.format(path, limit, offset + 1)));
        if(offset != 0)
            links.add(createLink(Link.RELATIONSHIP_PREVIOUS, String.format(path, limit, offset - 1)));
        return links;
    }

    List<Link> createBaseLinks(HttpServletRequest request, String selfPath){
        List<Link> links = new ArrayList<>();
        links.add(createLink(Link.RELATIONSHIP_SELF, selfPath));
        links.add(createLink(Link.RELATIONSHIP_BASEURI, getBaseUrl(request)));
        return links;
    }

    boolean authenticatedUserIsAdmin(HttpServletRequest request) {
        return true;
        //return request.isUserInRole("ADMIN");
    }

    String getAuthenticatedUserEmail(HttpServletRequest request){
        return "email3";
        //return request.getUserPrincipal().getName();
    }

    CollectionResource<GroupOutputModel> getAllGroups(
            String course,
            String semester,
            String id
    ) {
        return classService.getGroups(course, semester, id)
                .stream()
                .map(g -> new GroupOutputModel(g.getClassId(), g.getCourse(), g.getSemester(), g.getNumber()))
                .collect(Collectors.toCollection(CollectionResource::new));
    }

    CollectionResource<StudentOutputModel> getAllStudents(
            String course,
            String semester,
            String id
    ) {
        return classService.getStudents(course, semester, id)
                .stream()
                .map(s -> new StudentOutputModel(s.getNumber(), s.getName(), s.getEmail()))
                .collect(Collectors.toCollection(CollectionResource::new));
    }

    CollectionResource<TeacherOutputModel> getAllTeachers(
            String course,
            String semester,
            String id
    ) {
        return classService.getTeachers(course, semester, id)
                .stream()
                .map(t -> new TeacherOutputModel(t.getNumber(), t.getName(), t.getEmail()))
                .collect(Collectors.toCollection(CollectionResource::new));
    }

    Link createLink(String rel, String href){
        return LinkBuilder.newInstance()
                .setRelationship(rel)
                .setHref(href)
                .build();
    }

    Action createAction(String href, String name, List<Field> fields, Method method){
        return ActionBuilder.newInstance()
                        .setHref(href)
                        .setName(name)
                        .addFields(fields)
                        .setMethod(method)
                        .build();
    }

    Action createAction(String href, String name, Method method){
        return ActionBuilder.newInstance()
                .setHref(href)
                .setName(name)
                .setMethod(method)
                .build();
    }

    Field createField(String name, FieldType type){
        return FieldBuilder.createFieldBuilder()
                        .setName(name)
                        .setType(type)
                        .setRequired(true)
                        .build();
    }

    void setSubEntityUri(BaseResource subEntity, String uri){
        subEntity.setOverrideUri(uri);
    }

    String getPath(HttpServletRequest request){
        return String.valueOf(request.getRequestURL());
    }

    String getBaseUrl(HttpServletRequest request){
        String[] paths = getPath(request).split("/");
        return paths[0] + "//" + paths[2];
    }
}
