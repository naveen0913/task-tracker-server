package com.task.management.service;

import com.task.management.DTO.TaskRequest;
import com.task.management.model.TaskImages;
import com.task.management.model.Tasks;
import com.task.management.model.User;
import com.task.management.repository.TaskImageRepo;
import com.task.management.repository.TaskRepository;
import com.task.management.repository.UserRepository;
import com.task.management.response.ApiResponse;
import com.task.management.response.TaskImageResponse;
import com.task.management.response.TaskResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskImageRepo taskImageRepo;
    private final MailService mailService;
    private final NotificationService notificationService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    public ApiResponse createTask(TaskRequest request,
                                  List<MultipartFile> images,
                                  User user) {

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUrgency(request.getUrgency());
        task.setPriority(request.getPriority());
        task.setEndDateTime(request.getEndDateTime());
        task.setStatus(request.isStatus());
        task.setUser(user);
        task.setReminderSent(false);
        task.setCreatedAt(LocalDateTime.now());

        // Save Images
        taskRepository.save(task);
        saveTaskImages(task, images);
        notificationService.sendTaskCreatedNotification(user, request.getTitle());
        mailService.sendTaskCreatedMail(user.getEmail(), user.getUsername(), request.getTitle());
        return new ApiResponse<>(201, "created", null);
    }

    // ================= UPDATE TASK =================

    @Override
    public ApiResponse updateTask(Long taskId,
                                  TaskRequest request,
                                  List<MultipartFile> images) {

        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getUrgency() != null) {
            task.setUrgency(request.getUrgency());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getEndDateTime() != null) {
            task.setEndDateTime(request.getEndDateTime());
        }

        task.setUpdatedAt(LocalDateTime.now());

        if (images != null && !images.isEmpty()) {

//            task.getImages().clear();
            saveTaskImages(task, images);
        }

        taskRepository.save(task);
        notificationService.sendTaskDetailsUpdate(task.getUser(),task.getTitle());
        return new ApiResponse<>(200, "Task updated successfully", null);
    }

    @Transactional
    public ApiResponse deletedTaskById(Long taskId, User user) {

        Tasks task = taskRepository
                .findByTaskIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 2️⃣ Allow delete ONLY if completed
//        if (!task.isStatus()) {
//            return new ApiResponse<>(400,
//                    "Task must be completed before deleting", null);
//        }

        // 3️⃣ Get all images
        List<TaskImages> images =
                taskImageRepo.findByTasks(task);

        // 4️⃣ Delete files from disk
        for (TaskImages img : images) {

            try {
                String imagePath = img.getImageUrl()
                        .replace("/uploads/", "");

                Path filePath = Paths.get(uploadDir, imagePath);

                Files.deleteIfExists(filePath);

            } catch (Exception e) {
                throw new RuntimeException("Failed to delete image file");
            }
        }

        // 5️⃣ Delete image records
        taskImageRepo.deleteByTasks(task);

        // 6️⃣ Delete task
        taskRepository.delete(task);
        notificationService.sendTaskDeletedNotification(user, task.getTitle());
        return new ApiResponse<>(200, "Task deleted successfully", null);
    }

    @Transactional
    public ApiResponse updateTaskStatus(Long taskId, User user) {

        Tasks task = taskRepository
                .findByTaskIdAndUser(taskId, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // toggle status
        task.setStatus(!task.isStatus());

        task.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(task);
        notificationService.sendTaskStatusChangedNotification(user, task.getTitle());
        return new ApiResponse<>(
                200,
                "Task status updated",
                task.isStatus()
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse getAllTasks(User user) {
        List<Tasks> listByUser = taskRepository.getAllListByUser(user);
        List<TaskResponse> responses = listByUser.stream()
                .map(this::mapToResponse)
                .toList();
        return new ApiResponse<>(200, "ok", responses);
    }

    @Transactional(readOnly = true)
    public ApiResponse getTaskById(Long id){
        Tasks tasks = taskRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Task not found"));
        TaskResponse response = mapToResponse(tasks);
        return new ApiResponse<>(200, "ok", response);
    }

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void sendTaskReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(15);

        List<Tasks> tasks =
                taskRepository.findTasksForReminder(now, reminderTime);

        for (Tasks task : tasks) {

            User user = task.getUser();

            mailService.sendTaskReminderMail(
                    user.getEmail(),
                    user.getUsername(),
                    task.getTitle(),
                    task.getEndDateTime()
            );

            task.setReminderSent(true);
        }

        taskRepository.saveAll(tasks);
    }

    @Transactional
    public ApiResponse deleteTaskImage(Long image){
        TaskImages images = taskImageRepo.findById(image).orElseThrow(()-> new EntityNotFoundException("Image not found"));
        taskImageRepo.deleteById(image);
        return new ApiResponse(200,"deleted",null);
    }

    // ================= IMAGE SAVE LOGIC =================
    private void saveTaskImages(Tasks task, List<MultipartFile> images) {

        if (images == null || images.isEmpty()) return;

        try {
            Files.createDirectories(Paths.get(uploadDir));

            for (MultipartFile file : images) {

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                TaskImages taskImage = new TaskImages();
                taskImage.setImageUrl("/uploads/" + fileName);
                taskImage.setTasks(task);

                task.getImages().add(taskImage);
                taskImageRepo.save(taskImage);
            }

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed");
        }
    }


    private TaskResponse mapToResponse(Tasks task) {

        List<TaskImageResponse> images =
                task.getImages().stream()
                        .map(img -> new TaskImageResponse(
                                img.getImageId(),
                                "http://localhost:8081" + img.getImageUrl()
                        ))
                        .toList();

        return new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getUrgency().name(),
                task.getEndDateTime() != null
                        ? task.getEndDateTime().toString()
                        : null,
                task.isStatus(),
                task.getPriority().name(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                images,   // images included
                task.getUser().getId()
        );
    }


}
