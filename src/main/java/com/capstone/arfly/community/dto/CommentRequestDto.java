package com.capstone.arfly.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "댓글 작성 요청 정보")
public class CommentRequestDto {

    @NotBlank(message = "본문은 필수 입력 항목입니다.")
    @Schema(
            description = "댓글 본문 (멘션 포맷: @[닉네임](user:id))",
            example = "안녕하세요 @[홍길동](user:123) 님, 글 잘 읽었습니다!",
            requiredMode = RequiredMode.REQUIRED
    )
    private String content;

    @Schema(
            description = "멘션된 사용자 ID 목록",
            example = "[123, 124]"
    )
    private Set<Long> mentionedUserIds;
}
