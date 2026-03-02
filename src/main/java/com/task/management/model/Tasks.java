package com.task.management.model;

import com.task.management.Enum.Priority;
import com.task.management.Enum.Urgency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Urgency urgency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime endDateTime;

    private LocalDateTime updatedAt;


    @OneToMany(
            mappedBy = "tasks",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<TaskImages> images = new ArrayList<>();

    private boolean status;

    private boolean reminderSent;

}
