package com.example.taskflow.Service;

import com.example.taskflow.Repository.JobExecutionHistoryRepository;
import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.Worker.JobWorker;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobExecutionHistory;
import com.example.taskflow.entity.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobExecutionService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobWorker jobWorker;
    @Autowired
    private JobMetrics jobMetrics;

    @Autowired
    private JobExecutionHistoryRepository jobExecutionHistoryRepository;

    private static final Logger log =
            LoggerFactory.getLogger(JobExecutionService.class);

    @Transactional
    public void executeJob(Long id) {
        int claimed = jobRepository.claimScheduledJob(id, JobStatus.RUNNING, JobStatus.SCHEDULED, JobStatus.RETRY_WAIT);
        if (claimed == 0)
            return;
        log.info("Attempting to claim job {}", id);
        Job job = jobRepository.findById(id).orElseThrow();
        job.setStartedAt(LocalDateTime.now());
        job.setAttempts(job.getAttempts() + 1);
        log.info(
                "Starting job {} of type {}. Attempt {}/{}",
                job.getId(),
                job.getJobType(),
                job.getAttempts(),
                job.getMaxAttempts()
        );

        long startTime = System.currentTimeMillis();

        JobExecutionHistory history = new JobExecutionHistory();
        history.setJobId(job.getId());
        history.setAttemptNumber(job.getAttempts());
        history.setStartedAt(job.getStartedAt());
        history.setStatus(JobStatus.RUNNING);

        jobRepository.save(job);
        jobExecutionHistoryRepository.save(history);

        try {
            jobWorker.execute(job);
            job.setStatus(JobStatus.SUCCEEDED);
            history.setStatus(JobStatus.SUCCEEDED);
            jobMetrics.incrementSuccessfulJobs();
            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "Job {} completed successfully in {} ms",
                    job.getId(),
                    duration
            );
        } catch (Exception e) {
            log.error(
                    "Job {} failed on attempt {}/{}",
                    job.getId(),
                    job.getAttempts(),
                    job.getMaxAttempts(),
                    e
            );
            history.setErrorMessage(e.getMessage());
            if (job.getAttempts() < job.getMaxAttempts()) {
                job.setStatus(JobStatus.RETRY_WAIT);
                history.setStatus(JobStatus.RETRY_WAIT);
                jobMetrics.incrementRetriedJobs();
                long delaySeconds = 10L * (1L << (job.getAttempts() - 1));
                job.setScheduledAt(LocalDateTime.now().plusSeconds(delaySeconds));
            } else {
                job.setStatus(JobStatus.FAILED);
                history.setStatus(JobStatus.FAILED);
                jobMetrics.incrementFailedJobs();
            }

        } finally {
            history.setFinishedAt(LocalDateTime.now());
            jobExecutionHistoryRepository.save(history);
            jobRepository.save(job);
        }
    }
}
