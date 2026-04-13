package com.capstone.arfly.hospital.controller;

import com.capstone.arfly.common.exception.ErrorResponse;
import com.capstone.arfly.hospital.dto.HospitalListResponse;
import com.capstone.arfly.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Hospital", description = "지도 관련 API")
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "지도 리스트 조회", description = "사용자가 설정한 위치의 주변 10개의 동물병원을 가져온다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "병원 리스트 가져오기 성공"),
            @ApiResponse(responseCode = "500", description = "구글 api 문제",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/maps")
    public ResponseEntity<List<HospitalListResponse>> getHospitals(
            @AuthenticationPrincipal UserDetails userDetails
    ){

        Long userId = Long.parseLong(userDetails.getUsername());

        return new ResponseEntity<>(hospitalService.getHospitalList(userId),HttpStatus.OK);
    }
}
