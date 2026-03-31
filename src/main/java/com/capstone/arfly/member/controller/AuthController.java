package com.capstone.arfly.member.controller;

import com.capstone.arfly.common.exception.ErrorResponse;
import com.capstone.arfly.member.domain.Member;
import com.capstone.arfly.member.dto.MemberCreateDto;
import com.capstone.arfly.member.dto.TokenResponseDto;
import com.capstone.arfly.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "회원 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private  final AuthService authService;

    @Operation(summary = "회원가입", description = "사용자의 정보를 받아 회원가입 진행 후 토큰을 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공 및 토큰 발급",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(입력값 유효성 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디로 가입 시도",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody MemberCreateDto memberCreateDto) {
        //신규 유저 생성
        Member member = authService.create(memberCreateDto);
        //토큰 생성
        TokenResponseDto response = authService.generateTokens(member);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
