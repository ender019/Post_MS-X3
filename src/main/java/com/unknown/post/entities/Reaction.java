package com.unknown.post.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unknown.post.dtos.ReactTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@Document
public class Reaction {
    @Id
    @JsonIgnore
    private String id;
    private String user_id;
    private String reacted_id;
    private ReactTypes type;
    private LocalDateTime date;

    public Reaction(String user_id, String reacted_id, ReactTypes type) {
        this.user_id = user_id;
        this.reacted_id = reacted_id;
        this.type = type;
        this.date = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
