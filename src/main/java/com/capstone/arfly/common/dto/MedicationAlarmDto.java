package com.capstone.arfly.common.dto;

import com.capstone.arfly.notification.domain.DeviceType;
import java.time.LocalTime;

public record MedicationAlarmDto(
        Long medicationId, String title, String content, LocalTime reminderTime,
        Long fcmTokenId, String token, DeviceType deviceType) {
}
