package com.task.management.DTO;

import com.task.management.Enum.Priority;
import com.task.management.Enum.Urgency;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Urgency urgency;
    private LocalDateTime endDateTime;
    private boolean status;
    private Priority priority;
}
