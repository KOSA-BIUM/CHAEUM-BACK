package com.bium.chaeum.application.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealCardWithItemsRequest {
    private String calendarId;       // 우선: 소속 캘린더 ID (없으면 아래 userId+yearMonth 사용)
    private String userId;           // 대안: 캘린더 미보유 시 사용자 ID
    private String yearMonth;        // 대안: "YYYY-MM" 형식
    private LocalDateTime recordDate;     // 먹은 날짜/시간
    private String division;              // BREAKFAST/LUNCH/DINNER/SNACK
    private List<MealItemCreateRequest> items; // 음식 항목들
}
