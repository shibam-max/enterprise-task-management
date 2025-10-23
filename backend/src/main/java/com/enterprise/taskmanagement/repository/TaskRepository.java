package com.enterprise.taskmanagement.repository;

import com.enterprise.taskmanagement.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByAssigneeId(UUID assigneeId);
    
    List<Task> findByPriority(Task.TaskPriority priority);
    
    Page<Task> findByStatusAndAssigneeId(Task.TaskStatus status, UUID assigneeId, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Task> findTasksCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Task> searchByKeyword(@Param("keyword") String keyword);
    
    @Query(value = "SELECT * FROM tasks WHERE assignee_id = :assigneeId AND status = :status " +
                   "ORDER BY priority DESC, created_at ASC LIMIT :limit", nativeQuery = true)
    List<Task> findTopTasksByAssigneeAndStatus(@Param("assigneeId") UUID assigneeId, 
                                              @Param("status") String status, 
                                              @Param("limit") int limit);
}