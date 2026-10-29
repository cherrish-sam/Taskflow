package com.example.taskflow.Worker;

import com.example.taskflow.Service.JobRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobRecoveryScheduler {
    @Autowired
    private JobRecoveryService jobRecoveryService;

    @Scheduled(fixedDelay = 30000)
    public void recoverStaleJobs() {
        jobRecoveryService.recoverStaleJobs();
    }
}
