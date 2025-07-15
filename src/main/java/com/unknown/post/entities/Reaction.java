package com.unknown.post.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unknown.post.dtos.ReactTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public class Reaction {
    @Id
    @JsonIgnore
    private String id;
    private String user_id;
    private String post_id;
    private ReactTypes type;
    private LocalDateTime date;

    public Reaction(String user_id, String post_id, ReactTypes type) {
        this.user_id = user_id;
        this.post_id = post_id;
        this.type = type;
        this.date = LocalDateTime.now();
    }
}
