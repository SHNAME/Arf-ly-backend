package com.capstone.arfly.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 수정 요청 (변경할 필드만 전달)")
public class PostUpdateRequestDto {

    @Size(max = 50, message = "제목은 50자 이내로 입력해주세요.")
    @Schema(description = "수정할 게시글 제목", example = "우리 강아지 수정본!")
    private String title;

    @Schema(description = "수정할 게시글 내용", example = "다시 보니 더 귀엽네요.")
    private String content;

    @Schema(description = "삭제할 파일의 ID 리스트")
    List<Long>deleteFileIds;
}
