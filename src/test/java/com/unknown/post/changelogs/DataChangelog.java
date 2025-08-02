package com.unknown.post.changelogs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@ChangeUnit(id="2", order = "002", author = "X3_Admin")
public class DataChangelog {

    private final List<String> collections = List.of("posts", "comments", "post_reacts");

    private void insertJson(MongoTemplate mongoTemplate, String collectionName){
        ClassPathResource resource = new ClassPathResource("data/"+collectionName+".json");
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String jsonContent = new String(bytes, StandardCharsets.UTF_8);
            JsonNode rootNode = new ObjectMapper().readTree(jsonContent);
            for (JsonNode node : rootNode) mongoTemplate.insert(Document.parse(node.toString()), collectionName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Execution
    public void addData(MongoTemplate mongoTemplate) throws Exception {
        collections.forEach(collection -> insertJson(mongoTemplate, collection));
    }

    @RollbackExecution
    public void  rollback(MongoTemplate mongoTemplate) {
        collections.forEach(collection -> {
            if (mongoTemplate.collectionExists(collection)) {
                mongoTemplate.dropCollection(collection);
                mongoTemplate.createCollection(collection);
            }
        });
    }
}
