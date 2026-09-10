package com.projects.task_master.exceptions;

public class TeamDoesNotExist extends RuntimeException {
    public TeamDoesNotExist(String message) {
        super(message);
    }
}
