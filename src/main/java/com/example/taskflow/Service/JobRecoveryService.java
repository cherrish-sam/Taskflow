package com.example.taskflow.Service;

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

    @Transactional
    public void recoverStaleJobs(){
        LocalDateTime cutoff=LocalDateTime.now().minusMinutes(2);
        List<Job> staleJobs=jobRepository.findByStatusAndStartedAtBefore(JobStatus.RUNNING,cutoff);
        for (Job job : staleJobs) {
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
