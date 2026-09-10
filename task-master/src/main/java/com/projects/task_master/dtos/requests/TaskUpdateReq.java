package com.projects.task_master.dtos.requests;

import java.time.LocalDate;

public record TaskUpdateReq(
        String title,
        String description,
        LocalDate dueDate
) {
}
