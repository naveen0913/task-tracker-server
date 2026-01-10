package com.task.management.controller;

import com.task.management.model.Tasks;
import com.task.management.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addTask(@RequestBody Tasks tasks) {
        tasks.setCreatedAt(LocalDateTime.now());
        taskRepository.save(tasks);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(201);
    }

    @GetMapping("/all")
    public List<Tasks> getAllTasks() {
        return taskRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> markCompleted(@PathVariable Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tasks not found"));
        tasks.setStatus(true);
        tasks.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(tasks);
        return ResponseEntity.status(HttpStatus.OK.value()).body(200);
    }

    @DeleteMapping("/delete/{id}")
    public Tasks deleteTasks(@PathVariable Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tasks not found"));
        taskRepository.delete(tasks);
        return taskRepository.save(tasks);
    }

}
