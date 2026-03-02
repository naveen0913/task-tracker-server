package com.task.management.service;

import com.task.management.DTO.TaskRequest;
import com.task.management.model.Tasks;
import com.task.management.model.User;
import com.task.management.repository.TaskRepository;
import com.task.management.repository.UserRepository;
import com.task.management.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public interface TaskService {

    ApiResponse createTask(
            TaskRequest request,
            List<MultipartFile> images,
            User user
    );

    ApiResponse updateTask(
            Long taskId,
            TaskRequest request,
            List<MultipartFile> images
    );

}
