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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@ChangeUnit(id="2", order = "002", author = "X3_Admin")
public class DataChangelog {

    private void insertJson(MongoTemplate mongoTemplate, String collectionName) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/"+collectionName+".json");
        try (InputStream inputStream = resource.getInputStream()) {
            // 2. Чтение содержимого файла
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String jsonContent = new String(bytes, StandardCharsets.UTF_8);
            // 3. Парсинг JSON
            JsonNode rootNode = new ObjectMapper().readTree(jsonContent);
            // 5. Вставка в MongoDB
            for (JsonNode node : rootNode) mongoTemplate.insert(Document.parse(node.toString()), collectionName);
        }
    }

    @Execution
    public void addData(MongoTemplate mongoTemplate) throws Exception {
        insertJson(mongoTemplate, "posts");
        insertJson(mongoTemplate, "comments");
    }

    @RollbackExecution
    public void  rollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists("posts")) {
            mongoTemplate.dropCollection("posts");
            mongoTemplate.createCollection("posts");
        }
        if (mongoTemplate.collectionExists("comments")) {
            mongoTemplate.dropCollection("comments");
            mongoTemplate.createCollection("comments");
        }
    }
}
