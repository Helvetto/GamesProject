package com.mygame.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mygame.exception.GameException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, Object> resObj = new HashMap<>();
        String errorMsg = "Validation failed!";

        if (exception.getErrorCount() > 0) {
            List<String> errorDetails = new ArrayList<>();

            for (ObjectError error : exception.getBindingResult().getAllErrors()) {
                errorDetails.add(error.getDefaultMessage());
            }
            errorMsg = String.join(", ", errorDetails);
        }

        resObj.put("message", errorMsg);
        return ResponseEntity.badRequest().body(resObj);
    }

    @ExceptionHandler(GameException.class)
    public ResponseEntity<Object> handleGameException(GameException exception) {
        Map<String, Object> resObj = new HashMap<>();
        resObj.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(resObj);
    }
}
