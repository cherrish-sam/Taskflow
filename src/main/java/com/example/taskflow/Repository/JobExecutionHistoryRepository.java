package com.example.taskflow.Repository;

import com.example.taskflow.entity.JobExecutionHistory;
import com.example.taskflow.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobExecutionHistoryRepository extends JpaRepository<JobExecutionHistory,Long> {
    List<JobExecutionHistory> findByJobIdOrderByAttemptNumberAsc(Long jobId);

    Optional<JobExecutionHistory>
    findFirstByJobIdAndStatusOrderByAttemptNumberDesc(
            Long jobId,
            JobStatus status
    );
}
