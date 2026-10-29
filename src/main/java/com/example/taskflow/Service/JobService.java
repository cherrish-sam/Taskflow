package com.example.taskflow.Service;

import com.example.taskflow.Repository.JobExecutionHistoryRepository;
import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.dto.JobRequestDTO;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobExecutionHistory;
import com.example.taskflow.entity.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobExecutionHistoryRepository jobExecutionHistoryRepository;

    public Job createJob(JobRequestDTO jobRequestDTO){
        Job job=new Job();
        job.setJobType(jobRequestDTO.getJobType());
        job.setPayload(jobRequestDTO.getPayload());
        if(jobRequestDTO.getScheduledAt()==null)
            job.setScheduledAt(LocalDateTime.now());
        else
            job.setScheduledAt(jobRequestDTO.getScheduledAt());
        job.setStatus(JobStatus.SCHEDULED);
        return jobRepository.save(job);
    }
    public List<Job> findAllJobs(){
        return jobRepository.findAll();
    }
    public Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found: " + id));
    }

    public List<JobExecutionHistory> getExecutionHistory(Long id){
        if(!jobRepository.existsById(id))
            throw new RuntimeException("Job not found: "+id);
        return jobExecutionHistoryRepository.findByJobIdOrderByAttemptNumberAsc(id);
    }

}
