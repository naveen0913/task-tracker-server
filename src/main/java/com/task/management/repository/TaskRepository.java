package com.task.management.repository;

import com.task.management.model.Tasks;
import com.task.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tasks,Long> {


    @Query("""
       SELECT DISTINCT t
       FROM Tasks t
       LEFT JOIN FETCH t.images
       WHERE t.user = :user
       ORDER BY t.createdAt DESC
       """)
    List<Tasks> getAllListByUser(@Param("user") User user);


    Optional<Tasks> findByTaskIdAndUser(Long taskId, User user);

}
