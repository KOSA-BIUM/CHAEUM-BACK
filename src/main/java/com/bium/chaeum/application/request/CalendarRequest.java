package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CalendarRequest {
    private String userId;
    // e.g., "2025-09" (must match DB YEARMONTH format)
    private String yearMonth;
}
