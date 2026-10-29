package com.example.taskflow.Service;

import com.example.taskflow.Repository.JobExecutionHistoryRepository;
import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobRecoveryService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobExecutionHistoryRepository jobExecutionHistoryRepository;

    @Transactional
    public void recoverStaleJobs(){
        LocalDateTime cutoff=LocalDateTime.now().minusMinutes(2);
        List<Job> staleJobs=jobRepository.findByStatusAndStartedAtBefore(JobStatus.RUNNING,cutoff);
        for (Job job : staleJobs) {
            LocalDateTime recoveredAt = LocalDateTime.now();
            jobExecutionHistoryRepository
                    .findFirstByJobIdAndStatusOrderByAttemptNumberDesc(
                            job.getId(), JobStatus.RUNNING
                    ).ifPresent(history -> {
                        history.setStatus(JobStatus.FAILED);
                        history.setFinishedAt(recoveredAt);
                        history.setErrorMessage(
                                "Execution interrupted; recovered after stale RUNNING state"
                        );
                        jobExecutionHistoryRepository.save(history);
                    });
            if (job.getAttempts() < job.getMaxAttempts()) {
                job.setStatus(JobStatus.RETRY_WAIT);
                job.setScheduledAt(LocalDateTime.now());
            } else {
                job.setStatus(JobStatus.FAILED);
            }

            job.setStartedAt(null);
            jobRepository.save(job);

            System.out.println(
                    "Recovered stale job: " + job.getId()
            );
        }
    }
}
