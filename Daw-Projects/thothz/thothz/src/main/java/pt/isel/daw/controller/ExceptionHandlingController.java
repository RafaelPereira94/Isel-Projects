package pt.isel.daw.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.daw.exceptions.ResourceNotFound;
import pt.isel.daw.outputModel.errors.ErrorOutputModel;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@RestController
@RequestMapping(produces = "application/problem+json")
public class ExceptionHandlingController{

    @ExceptionHandler(value = {DataAccessException.class, SQLException.class})
    public ResponseEntity<ErrorOutputModel> dataAccessError(){
        return new ResponseEntity<>(
                new ErrorOutputModel(
                        500,
                        "Internal Server Error",
                        "There was an unexpected server error"
                ), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorOutputModel> emptyResultError(){
        return new ResponseEntity<>(
                new ErrorOutputModel(
                        404,
                        "Not found",
                        "The server has not found the resource"
                ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ResourceNotFound.class})
    public ResponseEntity<ErrorOutputModel> pageNotFound(){
        return new ResponseEntity<>(
                new ErrorOutputModel(
                        404,
                        "Not found",
                        "The server has not found the resource"
                ), HttpStatus.NOT_FOUND);
    }

}
