package com.example.taskflow.Worker;

import com.example.taskflow.Service.JobExecutionService;
import com.example.taskflow.entity.Job;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;


@Component
public class JobWorker {
    private static final Logger log =
            LoggerFactory.getLogger(JobExecutionService.class);
    public void execute(Job job){
        switch(job.getJobType()){
            case "SEND_EMAIL"-> log.info("Executing email job {}", job.getId());
            case "TEST_FAILURE" ->
                    throw new RuntimeException("Simulated failure");
            default -> throw new IllegalArgumentException( "Unsupported job type: " + job.getJobType());
        }
    }
}
