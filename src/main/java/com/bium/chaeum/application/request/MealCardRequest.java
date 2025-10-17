package com.bium.chaeum.application.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MealCardRequest {
    private String calendarId;       // 필수: 소속 캘린더 ID
    private LocalDateTime recordDate; // 필수: 기록 일시
    private String division;         // 필수: BREAKFAST/LUNCH/DINNER/SNACK
}
