package com.capstone.arfly.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "댓글 정보 응답")
public record CommentDetailResponseDto(
        @Schema(description = "댓글 ID", example = "1")
        Long commentId,

        @Schema(description = "작성자 Id", example = "123")
        Long authorId,

        @Schema(description = "작성자 닉네임", example = "유저123")
        String authorNickName,

        @Schema(description = "내용", example = "댓글 내용입니다.")
        String content,
        @Schema(description = "댓글 작성일")
        LocalDateTime createdAt
) {
}
