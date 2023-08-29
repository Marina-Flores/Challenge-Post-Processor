package com.compass.post.processor.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;

    private int userId;

    private String title;

    private String body;

    private List<CommentResponse> comments;
    private List<HistoryResponse> histories;
}
