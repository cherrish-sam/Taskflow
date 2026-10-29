package com.example.taskflow.Repository;

import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByStatusAndScheduledAtLessThanEqual(JobStatus status, LocalDateTime time);
    List<Job> findByStatusAndStartedAtBefore(JobStatus status,LocalDateTime time);

    @Modifying
    @Query("""
            UPDATE Job j
            SET j.status = :running
            WHERE j.id= :id
            AND j.status IN (:scheduled, :retrywait)
            """)
    int claimScheduledJob(
            @Param("id") Long id,
            @Param("running") JobStatus running,
            @Param("scheduled") JobStatus scheduled,
            @Param("retrywait") JobStatus retrywait
    );
}
