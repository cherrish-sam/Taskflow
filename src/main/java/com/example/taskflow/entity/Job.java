package com.example.taskflow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String jobType;
    @Column(nullable = false, columnDefinition = "JSON")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private JobStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime scheduledAt;
    private LocalDateTime updatedAt;
    @Column
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private int maxAttempts = 3;

    @PrePersist
    protected void onCreate(){
        LocalDateTime now=LocalDateTime.now();
        if(createdAt==null)
            createdAt=now;
        if(scheduledAt==null)
            scheduledAt=now;
        if(status==null)
            status=JobStatus.SCHEDULED;
        updatedAt=now;

    }
    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

}
