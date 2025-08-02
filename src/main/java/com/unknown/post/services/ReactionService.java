package com.unknown.post.services;

import com.unknown.post.dtos.ReactDTO;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReactionService {
    private final MongoTemplate mongoTemplate;

    public List<ReactDTO> getReactionsCountByCollection(String collection, String reacted_id) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("reacted_id").is(reacted_id)),
                        Aggregation.group("type").count().as("count"),
                        Aggregation.project("count").and("_id").as("name")
                ),
                collection,
                ReactDTO.class
        ).getMappedResults();
    }

    public List<String> getReactionsByUser(String collection, String user_id, String type) {
        var query = new Query(Criteria.where("user_id").is(user_id).and("type").is(type));
        query.fields().include("reacted_id");
        return mongoTemplate.find(query, String.class, collection);
    }

    public List<ReactDTO> getReactionsCountByUser(String collection, String user_id) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("user_id").is(user_id)),
                        Aggregation.group("type").count().as("count"),
                        Aggregation.project("count").and("_id").as("name")
                ),
                collection,
                ReactDTO.class
        ).getMappedResults();
    }

    public Reaction procReaction(String collection, UReactDTO data) {
        var reaction = Optional.ofNullable(mongoTemplate.findOne(new Query(
                        Criteria.where("reacted_id").is(data.reacted_id()).and("user_id").is(data.user_id())
                ), Reaction.class, collection
        ));
        if (reaction.isEmpty())
            return mongoTemplate.save(new Reaction(data.user_id(), data.reacted_id(), data.type()), collection);
        else if (reaction.get().getType().equals(data.type())) {
            mongoTemplate.remove(reaction.get(), collection);
            return reaction.get();
        }
        reaction.get().setType(data.type());
        return mongoTemplate.save(reaction.get(), collection);
    }

    public void deleteReactionById(String collection, List<String> reacted_id) {
        mongoTemplate.remove(new Query(Criteria.where("reacted_id").is(reacted_id)), collection);
    }

    public void deleteReactionByUser(String collection, String user_id) {
        mongoTemplate.remove(new Query(Criteria.where("user_id").is(user_id)), collection);
    }

    public void replaceUser(String collection, String old_id, String new_id) {
        mongoTemplate.updateMulti(new Query(Criteria.where("user_id").is(old_id)),
                new Update().set("user_id", new_id), collection
        );
    }
}
