package pt.isel.daw.controller;

import com.google.code.siren4j.error.Siren4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pt.isel.daw.service.ClassService;
import pt.isel.daw.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RootController extends Controller{


    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    RootController(TeacherService teacherService, ClassService classService, RequestMappingHandlerMapping handlerMapping) {
        super(teacherService, classService);
        this.handlerMapping = handlerMapping;
    }

    @GetMapping(name = "index")
    public ResponseEntity<Map<String, String>> getAllEndpoints(HttpServletRequest request) throws Siren4JException {
        String fullPath = getPath(request);
        String path = fullPath.substring(0, fullPath.length() - 1);
        Map<String, String> c = new HashMap<>();
                handlerMapping
                .getHandlerMethods()
                .forEach((i, h) -> {
                    if (i.getMethodsCondition().getMethods().contains(RequestMethod.GET))
                        c.put(i.getName() + "_url", path + i.getPatternsCondition().getPatterns().toArray()[0]);
                });

        return ResponseEntity.ok(c);
    }

}
