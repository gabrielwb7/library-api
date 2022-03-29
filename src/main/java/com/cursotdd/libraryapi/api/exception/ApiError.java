package com.cursotdd.libraryapi.api.exception;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiError {

    private List<String> errors;

    public ApiError(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiError(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiError(ResponseStatusException ex) {
        this.errors = Arrays.asList(ex.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
