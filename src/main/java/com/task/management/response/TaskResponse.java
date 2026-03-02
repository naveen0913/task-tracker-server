package com.task.management.response;

import com.task.management.Enum.Priority;
import com.task.management.Enum.Urgency;
import com.task.management.model.TaskImages;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private String urgency;
    private String endDateTime;
    private boolean status;
    private String priority;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<TaskImageResponse> images;
    private Long userId;
}
