package pt.isel.daw.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.daw.outputModel.errors.ErrorOutputModel;

/**
 * To handle the error mapping that spring boot has
 */

@RestController
public class ApiErrorController implements ErrorController {

    private final static String ERROR_PATH = "/error";

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(value = ERROR_PATH, produces = "application/problem+json")
    public ResponseEntity<ErrorOutputModel> internalServerError(){
        return new ResponseEntity<>(
                new ErrorOutputModel(
                        404,
                        "Not found",
                        "The server has not found anything matching the Request-URI"
                ), HttpStatus.NOT_FOUND);
    }

}
