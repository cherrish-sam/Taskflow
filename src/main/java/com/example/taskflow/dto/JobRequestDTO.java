package com.example.taskflow.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class JobRequestDTO {
    @NotBlank(message = "Job type is Required")
    @Size(max = 100, message = "Job type cannot exceed 100 Characters" )
    private String jobType;

    @NotBlank(message = "Payload is Required")
    private String payload;
    private LocalDateTime scheduledAt;
}
