package com.unknown.post.dtos;

public record UReactDTO(
        String user_id,
        String reacted_id,
        ReactTypes type
) {}
