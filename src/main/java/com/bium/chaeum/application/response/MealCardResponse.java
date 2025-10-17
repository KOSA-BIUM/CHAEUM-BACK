package com.bium.chaeum.application.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.bium.chaeum.domain.model.entity.MealCard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MealCardResponse {
    private String mealCardId;
    private String calendarId;
    private LocalDateTime recordDate;
    private String division;
    private List<MealItemResponse> mealItems;

    public static MealCardResponse from(MealCard mealCard) {
        if (mealCard == null) return null;
        return new MealCardResponse(
            mealCard.getId().value(),
            mealCard.getCalendarId().value(),
            mealCard.getRecordDate(),
            mealCard.getDivision().name(),
            mealCard.getMealItems() == null ? List.of() : mealCard.getMealItems().stream().map(MealItemResponse::from).collect(Collectors.toList())
        );
    }
}
