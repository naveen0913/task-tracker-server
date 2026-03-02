package com.task.management.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskImageResponse {
    private Long id;
    private String imageUrl;
}
