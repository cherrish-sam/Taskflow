package com.example.taskflow.controller;

import com.example.taskflow.Service.JobService;
import com.example.taskflow.dto.JobRequestDTO;
import com.example.taskflow.entity.Job;
import com.example.taskflow.entity.JobExecutionHistory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @PostMapping
    public ResponseEntity<Job> createJob(@Valid @RequestBody JobRequestDTO jobRequestDTO){
        Job job=jobService.createJob(jobRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs(){
        List<Job> jobs=jobService.findAllJobs();
        return ResponseEntity.status(HttpStatus.OK).body(jobs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Job> findJobById(@PathVariable Long id){
        Job job=jobService.findJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<JobExecutionHistory>> getExecutionHistory(@PathVariable Long id){
        return ResponseEntity.ok(jobService.getExecutionHistory(id));
    }

}
