package com.projects.task_master.handlers;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ApiErrorResponse<T> {

    private Boolean success;
    private String message;
    private Integer statusCode;
    private String path;
    private Instant timestamp;
    private List<String> errors;


}
