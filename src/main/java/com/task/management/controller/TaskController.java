package com.task.management.controller;

import com.task.management.DTO.TaskRequest;
import com.task.management.model.Tasks;
import com.task.management.repository.TaskRepository;
import com.task.management.response.ApiResponse;
import com.task.management.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
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
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskService taskService;

//    @PostMapping("/add")
//    public ResponseEntity<ApiResponse<?>> addTask(@RequestBody Tasks tasks) {
//        tasks.setCreatedAt(LocalDateTime.now());
//
//        Tasks savedTask = taskRepository.save(tasks);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>(
//                        201,
//                        "Task added successfully",
//                        null
//                ));
//    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createTask(
            @RequestPart("task") TaskRequest taskRequest,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @PathVariable String username
    ) {

        Tasks response = taskService.createTask(taskRequest, images, username);
        return ResponseEntity.status(201).body("created");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Tasks>>> getAllTasks() {
        List<Tasks> tasks = taskRepository.findAll();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Tasks fetched successfully",
                        tasks
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> markCompleted(@PathVariable Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tasks not found"));
        tasks.setTitle(ti);
        tasks.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(tasks);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Tasks Updated successfully",
                        null
                )
        );    }

    @DeleteMapping("/delete/{id}")
    public Tasks deleteTasks(@PathVariable Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tasks not found"));
        taskRepository.delete(tasks);
        return taskRepository.save(tasks);
    }

}
