package com.capstone.arfly.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "복약 알람 활성화 상태 수정 요청")
public class UpdateMedicationReminderStatusRequest {

    @Schema(
            description = "알람 활성화 여부 (true: 활성, false: 비활성)",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "알람 활성화 여부는 필수 항목입니다.")
    private Boolean active;
}