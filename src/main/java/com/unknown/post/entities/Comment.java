package com.unknown.post.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "comments")
public class Comment {
    @Id
    @JsonIgnore
    private String id;
    private String content;
    private String author;
    @JsonIgnore
    private String post_id;
    private LocalDateTime date;


    public Comment(String content, String author, String post_id) {
        this.content = content;
        this.author = author;
        this.post_id = post_id;
        this.date = LocalDateTime.now();
    }
}
