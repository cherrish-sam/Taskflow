package com.example.taskflow.Service;

import com.example.taskflow.Repository.JobExecutionHistoryRepository;
import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.Worker.JobWorker;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobExecutionServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobWorker jobWorker;

    @Mock
    private JobMetrics jobMetrics;

    @Mock
    private JobExecutionHistoryRepository jobExecutionHistoryRepository;

    @InjectMocks
    private JobExecutionService jobExecutionService;

    private Job job;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(1L);
        job.setJobType("TEST_FAILURE");
        job.setPayload("{\"test\":true}");
        job.setStatus(JobStatus.SCHEDULED);
        job.setAttempts(0);
        job.setMaxAttempts(3);
    }
    private void mockSuccessfulClaim() {
        when(jobRepository.claimScheduledJob(
                1L,
                JobStatus.RUNNING,
                JobStatus.SCHEDULED,
                JobStatus.RETRY_WAIT
        )).thenReturn(1);

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldSetRetryWaitWhenAttemptsRemain() {
        mockSuccessfulClaim();

        doThrow(new RuntimeException("Simulated failure"))
                .when(jobWorker).execute(job);

        jobExecutionService.executeJob(1L);

        assertEquals(1, job.getAttempts());
        assertEquals(JobStatus.RETRY_WAIT, job.getStatus());
        assertEquals(2, job.getMaxAttempts() - job.getAttempts());

        verify(jobWorker, times(1)).execute(job);
    }

    @Test
    void shouldMarkJobFailedWhenMaxAttemptsReached() {
        job.setAttempts(2);
        mockSuccessfulClaim();

        doThrow(new RuntimeException("Simulated failure"))
                .when(jobWorker).execute(job);

        jobExecutionService.executeJob(1L);

        assertEquals(3, job.getAttempts());
        assertEquals(JobStatus.FAILED, job.getStatus());

        verify(jobWorker, times(1)).execute(job);
    }

    @Test
    void shouldMarkJobSucceededWhenExecutionSucceeds() {
        mockSuccessfulClaim();

        jobExecutionService.executeJob(1L);

        assertEquals(1, job.getAttempts());
        assertEquals(JobStatus.SUCCEEDED, job.getStatus());

        verify(jobWorker, times(1)).execute(job);
    }

    @Test
    void shouldNotExecuteJobWhenClaimFails() {
        when(jobRepository.claimScheduledJob(
                1L,
                JobStatus.RUNNING,
                JobStatus.SCHEDULED,
                JobStatus.RETRY_WAIT
        )).thenReturn(0);

        jobExecutionService.executeJob(1L);

        verify(jobRepository, never()).findById(1L);
        verify(jobWorker, never()).execute(any(Job.class));
    }


    @Test
    void shouldNotExecuteJobWhenAlreadyClaimed() {
        when(jobRepository.claimScheduledJob(
                1L,
                JobStatus.RUNNING,
                JobStatus.SCHEDULED,
                JobStatus.RETRY_WAIT
        )).thenReturn(0);

        jobExecutionService.executeJob(1L);

        verify(jobRepository, times(1)).claimScheduledJob(
                1L,
                JobStatus.RUNNING,
                JobStatus.SCHEDULED,
                JobStatus.RETRY_WAIT
        );

        verify(jobRepository, never()).findById(1L);
        verify(jobWorker, never()).execute(any(Job.class));
    }



}
