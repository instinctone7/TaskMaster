package com.projects.task_master.exceptions;

public class NonAuthorized extends RuntimeException {
    public NonAuthorized(String message) {
        super(message);
    }
}
