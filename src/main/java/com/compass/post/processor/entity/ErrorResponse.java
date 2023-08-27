package com.compass.post.processor.entity;

import java.sql.Timestamp;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    public String message;
    public Timestamp timestamp;
    public String httpStatus;
}