package com.example.taskflow.Worker;

import com.example.taskflow.Repository.JobRepository;
import com.example.taskflow.Service.JobExecutionService;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobScheduler {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobWorker jobWorker;

    @Autowired
    private JobExecutionService jobExecutionService;

    @Scheduled(fixedDelay = 5000)
    public void scheduleJobs(){
        List<Job> jobList=new ArrayList<>();
        jobList.addAll(jobRepository.findByStatusAndScheduledAtLessThanEqual(JobStatus.SCHEDULED, LocalDateTime.now()));
        jobList.addAll(jobRepository.findByStatusAndScheduledAtLessThanEqual(JobStatus.RETRY_WAIT,LocalDateTime.now()));
        for(Job j:jobList)
            jobExecutionService.executeJob(j.getId());
    }


}
