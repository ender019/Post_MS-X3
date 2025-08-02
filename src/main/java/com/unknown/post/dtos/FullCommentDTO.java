package com.unknown.post.dtos;

import java.time.LocalDateTime;

public record FullCommentDTO(
        String user_id,
        String username,
        String avatar,
        String comment_id,
        String content,
        LocalDateTime date
) {}
