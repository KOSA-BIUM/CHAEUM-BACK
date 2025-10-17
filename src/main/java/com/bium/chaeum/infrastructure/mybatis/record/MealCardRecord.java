package com.bium.chaeum.infrastructure.mybatis.record;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealCardRecord {
    private String mealCardId;
    private String calendarId;
    private LocalDateTime recordDate;    
    private String division;    	// enum : BREAKFAST/LUNCH/DINNER/SNACK
    private List<MealItemRecord> mealItems;
}
