package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarRequest {
    private String userId;
    // e.g., "2025-09" (must match DB YEARMONTH format)
    private String yearMonth;
}
