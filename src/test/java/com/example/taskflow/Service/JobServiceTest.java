package com.example.taskflow.Service;

import com.example.taskflow.dto.JobRequestDTO;
import com.example.taskflow.entity.Job;
import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.entity.JobStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void createJobShouldSaveScheduledJob(){
        JobRequestDTO request = new JobRequestDTO();
        request.setJobType("SEND_EMAIL");
        request.setPayload("{\"to\":\"user@example.com\"}");

        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> {
                    Job job = invocation.getArgument(0);
                    job.setId(1L);
                    return job;
                });
        Job savedJob = jobService.createJob(request);
        assertNotNull(savedJob);
        assertEquals(1L, savedJob.getId());
        assertEquals("SEND_EMAIL", savedJob.getJobType());
        assertEquals("{\"to\":\"user@example.com\"}", savedJob.getPayload());
        assertEquals(JobStatus.SCHEDULED, savedJob.getStatus());
        assertNotNull(savedJob.getScheduledAt());

        verify(jobRepository, times(1)).save(any(Job.class));
    }
}
