package com.example.taskflow.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Component;

@Component
public class JobMetrics {
    private final Counter successfulJobs;
    private final Counter failedJobs;
    private final Counter retriedJobs;

    public JobMetrics(MeterRegistry meterRegistry){
        this.successfulJobs = Counter.builder("taskflow.jobs.success")
                .description("Total number of successfully executed jobs")
                .register(meterRegistry);

        this.failedJobs = Counter.builder("taskflow.jobs.failure")
                .description("Total number of jobs that permanently failed")
                .register(meterRegistry);

        this.retriedJobs = Counter.builder("taskflow.jobs.retry")
                .description("Total number of jobs scheduled for retry")
                .register(meterRegistry);
    }
    public void incrementSuccessfulJobs() {
        successfulJobs.increment();
    }

    public void incrementFailedJobs() {
        failedJobs.increment();
    }

    public void incrementRetriedJobs() {
        retriedJobs.increment();
    }

}
