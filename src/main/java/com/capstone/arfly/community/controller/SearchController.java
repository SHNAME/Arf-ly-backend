package com.capstone.arfly.community.controller;

import com.capstone.arfly.community.dto.GetRecentSearchResponseDto;
import com.capstone.arfly.community.service.SearchService;
import com.capstone.arfly.member.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/search/recent")
public class SearchController {

    private final SearchService searchService;

    @Operation(
            summary = "최근 검색 기록 불러오기",
            description = "현재 로그인한 사용자의 최근 검색 기록을 불러옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 기록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 혹은 유효하지 않은 토큰)")
    })
    @GetMapping()
    public ResponseEntity<?> getRecentSearches(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        long userId = Long.parseLong(userDetails.getUsername());
        List<GetRecentSearchResponseDto> response = searchService.getRecentSearchHistory(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
