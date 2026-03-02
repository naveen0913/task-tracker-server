package com.task.management.repository;

import com.task.management.model.TaskImages;
import com.task.management.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskImageRepo extends JpaRepository<TaskImages,Long> {

    List<TaskImages> findByTasks(Tasks tasks);

    void deleteByTasks(Tasks tasks);
}
