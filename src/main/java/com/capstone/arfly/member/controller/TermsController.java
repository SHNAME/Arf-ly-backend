package com.capstone.arfly.member.controller;

import com.capstone.arfly.member.dto.LatestTermsResponseDto;
import com.capstone.arfly.member.service.TermsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {
    private final TermsService termsService;

    @Operation(
            summary = "최신 약관 목록 조회",
            description = "현재 시스템에 등록된 최신 버전의 약관 리스트를 순서(orderIndex)에 맞춰 반환. 회원가입 시 약관 동의 화면 구성에 사용."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "약관 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LatestTermsResponseDto.class))
                    )
            )
    })
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAgreements() {
        List<LatestTermsResponseDto> response = termsService.getLatestAgreements();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "최신 약관 동의 여부 확인",
            description = "현재 로그인한 사용자가 최신 버전의 모든 필수 약관에 동의했는지 여부를 반환."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동의 여부 확인 성공 (true: 모두 동의 / false: 미동의 항목 존재)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 혹은 유효하지 않은 토큰)")
    })
    @GetMapping("/latest/agreement-status")
    public ResponseEntity<?> checkLatestTermsAgreement(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        boolean agreed = termsService.hasAgreedToLatestTerms(memberId);
        return new ResponseEntity<>(agreed, HttpStatus.OK);
    }
}
