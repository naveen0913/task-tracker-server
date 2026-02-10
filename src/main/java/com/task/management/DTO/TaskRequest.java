package com.task.management.DTO;

import com.task.management.Enum.Priority;
import com.task.management.Enum.Urgency;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Priority priority;
    private Urgency urgency;
    private String imageUrl;
}
