package com.task.management.repository;

import com.task.management.model.Tasks;
import com.task.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tasks,Long> {
    List<Task> findByUser(User user);

}
