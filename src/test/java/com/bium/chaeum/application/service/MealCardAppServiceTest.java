package com.bium.chaeum.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.request.CalendarRequest;
import com.bium.chaeum.application.request.MealCardWithItemsRequest;
import com.bium.chaeum.application.request.MealItemCreateRequest;
import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.CalendarResponse;
import com.bium.chaeum.application.response.MealCardResponse;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.domain.shared.error.DomainException;

@SpringBootTest
@Transactional
class MealCardAppServiceTest {

    @Autowired
    private MealCardAppService mealCardAppService;
    @Autowired
    private CalendarAppService calendarAppService;
    @Autowired
    private UserAppService userAppService;

    private String userId;
    private String calendarId;
    private final String yearMonth = "2025-10";

    @BeforeEach
    void setUpUser() {
        String email = "test+" + java.util.UUID.randomUUID() + "@local";
        String password = "Secret123!";
        String name = "Tester";
        UserResponse u = userAppService.register(SignUpRequest.builder()
            .email(email)
            .password(password)
            .name(name)
            .build());
        this.userId = u.getUserId();
        CalendarResponse cal = calendarAppService.ensureExists(CalendarRequest.builder()
                .userId(userId)
                .yearMonth(yearMonth)
                .build());
        this.calendarId = cal.getCalendarId();
    }

    @Test
    @DisplayName("createWithItemsByCalendarId: create using existing calendarId")
    void createWithItemsByCalendarId_success() {
        // given
        LocalDateTime recordDate = LocalDateTime.of(2025, 10, 2, 8, 0);

        MealCardWithItemsRequest req = MealCardWithItemsRequest.builder()
        	.calendarId(calendarId)
            .recordDate(recordDate)
            .division("BREAKFAST")
            .items(List.of(
                    MealItemCreateRequest.builder().name("오트밀").ingredient("귀리 60g, 우유 200ml").carbohydrate(50).protein(12).fat(6).sodium(200).calorie(300).build(),
                    MealItemCreateRequest.builder().name("삶은 계란").ingredient("계란 2개").carbohydrate(2).protein(12).fat(10).sodium(120).calorie(140).build(),
                    MealItemCreateRequest.builder().name("구운 계란").ingredient("계란 2개").carbohydrate(2).protein(12).fat(10).sodium(120).calorie(140).build()
            ))
            .build();
        
        // when
        MealCardResponse res = mealCardAppService.createWithItemsByCalendarId(calendarId, req);

        // then
        assertThat(res.getMealCardId()).isNotBlank();
        assertThat(res.getCalendarId()).isEqualTo(calendarId);
        assertThat(res.getRecordDate()).isEqualTo(recordDate);
        assertThat(res.getDivision()).isEqualTo("BREAKFAST");
        assertThat(res.getMealItems()).hasSize(3);
    }

    @Test
    @DisplayName("createWithItems: duplicate (calendarId, recordDate, division) throws DomainException")
    void createWithItems_duplicate_throws() {
        // given
        LocalDateTime recordDate = LocalDateTime.of(2025, 10, 2, 8, 0);
        MealCardWithItemsRequest req = MealCardWithItemsRequest.builder()
        	.calendarId(calendarId)            
            .recordDate(recordDate)
            .division("BREAKFAST")
            .items(List.of(MealItemCreateRequest.builder().name("오트밀").build()))
            .build();

        mealCardAppService.createWithItemsByCalendarId(calendarId,req);

        // when/then
        assertThatThrownBy(() -> mealCardAppService.createWithItemsByCalendarId(calendarId, req))
            .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("getDetailByMealCardId: returns mealcard with items")
    void getDetail_returns_items() {
        // given
        LocalDateTime recordDate = LocalDateTime.of(2025, 10, 2, 12, 0);
        MealCardResponse created = mealCardAppService.createWithItemsByCalendarId(calendarId,MealCardWithItemsRequest.builder()
        	.calendarId(calendarId)    
            .recordDate(recordDate)
            .division("LUNCH")
            .items(List.of(MealItemCreateRequest.builder().name("샐러드").protein(8).calorie(120).build()))
            .build());

        // when
        var got = mealCardAppService.getDetailByMealCardId(created.getMealCardId());

        // then
        assertThat(got).isPresent();
        assertThat(got.get().getMealItems()).hasSize(1);
        assertThat(got.get().getMealItems().get(0).getName()).isEqualTo("샐러드");
    }
}
