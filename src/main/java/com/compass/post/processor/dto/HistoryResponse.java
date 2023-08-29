package com.compass.post.processor.dto;

import java.util.Date;

import com.compass.post.processor.enums.PostState;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private Long id; 

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private PostState status;
}
