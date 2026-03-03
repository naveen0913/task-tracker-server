package com.task.management.controller;

import com.task.management.DTO.TaskRequest;
import com.task.management.config.AuthHelper;
import com.task.management.model.Tasks;
import com.task.management.model.User;
import com.task.management.repository.TaskRepository;
import com.task.management.response.ApiResponse;
import com.task.management.service.TaskService;
import com.task.management.service.TaskServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskServiceImpl taskService;
    private final AuthHelper authHelper;


    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createTask(
            @ModelAttribute TaskRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest httpRequest) {

        User user = authHelper.getUserFromRequest(httpRequest);

        ApiResponse task = taskService.createTask(request, images, user);

        return ResponseEntity
                .status(task.getCode())
                .body(task);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks(HttpServletRequest request) {
        User user = authHelper.getUserFromRequest(request);
        ApiResponse response = taskService.getAllTasks(user);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllTasks(HttpServletRequest request,@PathVariable Long id) {
        User user = authHelper.getUserFromRequest(request);
        ApiResponse response = taskService.getTaskById(id);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }


    @PatchMapping("/status/{taskId}")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            HttpServletRequest request) {

        User user = authHelper.getUserFromRequest(request);

        ApiResponse response =
                taskService.updateTaskStatus(taskId, user);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long taskId,
            HttpServletRequest request) {

        User user = authHelper.getUserFromRequest(request);

        ApiResponse response =
                taskService.deletedTaskById(taskId, user);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }

    @DeleteMapping("/delete/image/{taskId}")
    public ResponseEntity<?> deleteTaskImage(
            @PathVariable Long taskId,
            HttpServletRequest request) {

        User user = authHelper.getUserFromRequest(request);

        ApiResponse response =
                taskService.deleteTaskImage(taskId);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }


    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long taskId,
            @ModelAttribute TaskRequest taskRequest,
            @RequestParam(value = "images",required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        User user = authHelper.getUserFromRequest(request);

        ApiResponse response =
                taskService.updateTask(taskId,taskRequest, images);

        return ResponseEntity
                .status(response.getCode())
                .body(response);
    }

}
