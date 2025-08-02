package com.unknown.post.changelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id="1", order = "001", author = "X3_Admin", transactional = false)
public class CreateCollections {
    @Execution
    public void createCollections(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("posts"))
            mongoTemplate.createCollection("posts");
        if (!mongoTemplate.collectionExists("comments"))
            mongoTemplate.createCollection("comments");
        if (!mongoTemplate.collectionExists("post_reacts"))
            mongoTemplate.createCollection("post_reacts");
        if (!mongoTemplate.collectionExists("comment_reacts"))
            mongoTemplate.createCollection("comment_reacts");
    }

    @RollbackExecution
    public void  rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("posts");
        mongoTemplate.dropCollection("comments");
        mongoTemplate.dropCollection("post_reacts");
        mongoTemplate.dropCollection("comment_reacts");
    }
}
