package com.unknown.post.services;

import com.unknown.post.dtos.ReactDTO;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReactionService {
    private final MongoTemplate mongoTemplate;

    public List<ReactDTO> getReactionsCountByPost(String collection, String post_id) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("post_id").is(post_id)),
                        Aggregation.group("type").count().as("count"),
                        Aggregation.project().and("type").as("type").and("count").as("count")
                ),
                collection,
                ReactDTO.class
        ).getMappedResults();
    }

    public List<String> getReactionsByUser(String collection, String user_id, String type) {
        var query = new Query(Criteria.where("user_id").is(user_id).and("type").is(type));
        query.fields().include("_id");
        return mongoTemplate.find(query, String.class, collection
        );
    }

    public List<ReactDTO> getReactionsCountByUser(String collection, String user_id) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("user_id").is(user_id)),
                        Aggregation.group("type").count().as("count"),
                        Aggregation.project().and("_id").as("user_id").and("posts").as("posts")
                ),
                collection,
                ReactDTO.class
        ).getMappedResults();
    }

    public Reaction procReaction(String collection, UReactDTO data) {
        var reaction = Optional.ofNullable(mongoTemplate.findOne(new Query(
                        Criteria.where("post_id").is(data.post_id()).and("user_id").is(data.user_id())
                ), Reaction.class, collection
        ));
        if (reaction.isEmpty())
            return mongoTemplate.save(new Reaction(data.user_id(), data.post_id(), data.type()), collection);
        else if (reaction.get().getType().equals(data.type())) {
            mongoTemplate.remove(reaction.get(), collection);
            return reaction.get();
        }
        reaction.get().setType(data.type());
        return mongoTemplate.save(reaction.get(), collection);
    }
}
