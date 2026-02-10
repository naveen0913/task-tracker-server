package com.task.management.service;

import com.task.management.DTO.TaskRequest;
import com.task.management.model.Tasks;
import com.task.management.model.User;
import com.task.management.repository.TaskRepository;
import com.task.management.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Tasks createTask(
            TaskRequest request,
            MultipartFile[] images,
            String username
    ) {
        User user = userRepository.findByUsername(username).orElseThrow();

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setUrgency(request.getUrgency());
        task.setUser(user);

        Tasks savedTask = taskRepository.save(task);

        if (images != null && images.length > 0) {
            List<String> imageUrls =
                    saveTaskImages(savedTask.getId(), images);
            savedTask.setImageUrls(imageUrls);
            taskRepository.save(savedTask);
        }

        return savedTask;
    }

    public List<String> saveTaskImages(Long taskId, MultipartFile[] files) {
        List<String> imageUrls = new ArrayList<>();

        try {
            Path taskDir = Paths.get(uploadDir, "tasks", taskId.toString());
            Files.createDirectories(taskDir);

            for (MultipartFile file : files) {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = taskDir.resolve(filename);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                imageUrls.add("/uploads/tasks/" + taskId + "/" + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store images", e);
        }

        return imageUrls;
    }

}
