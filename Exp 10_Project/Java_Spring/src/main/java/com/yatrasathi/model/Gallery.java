package com.yatrasathi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "galleries")
public class Gallery {
    @Id
    private String id;
    private String username;
    private String caption;
    private String dham;
    private String imageUrl;
    private List<String> likes = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
}