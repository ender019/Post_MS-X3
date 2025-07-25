package com.unknown.post.dtos;

import java.time.LocalDateTime;

public record FullPostDTO(
        String user_id,
        String username,
        String avatar,
        String post_id,
        String title,
        String content,
        LocalDateTime date
) {}
