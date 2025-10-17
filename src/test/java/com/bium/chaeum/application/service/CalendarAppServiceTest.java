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
import com.bium.chaeum.application.request.MealCardRequest;
import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.CalendarResponse;
import com.bium.chaeum.application.response.MealCardResponse;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.domain.shared.error.DomainException;

@SpringBootTest
@Transactional
class CalendarAppServiceTest {

    @Autowired
    private CalendarAppService calendarAppService;
    @Autowired
    private MealCardAppService mealCardAppService;
    @Autowired
    private UserAppService userAppService;

    private String userId;

    @BeforeEach
    void setUpUser() {
        String email = "test+" + java.util.UUID.randomUUID() + "@local";
        String password = "Secret123!";
        String name = "Tester";
        UserResponse userResponse = userAppService.register(SignUpRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .build());
        this.userId = userResponse.getUserId();
    }

    @Test
    @DisplayName("ensureExists: creates calendar if missing and returns it")
    void ensureExists_creates() {
        // given
        String yearMonth = "2025-10";

        // when
        CalendarResponse res = calendarAppService.ensureExists(CalendarRequest.builder()
                .userId(userId)
                .yearMonth(yearMonth)
                .build());

        // then
        assertThat(res.getUserId()).isEqualTo(userId);
        assertThat(res.getYearMonth()).isEqualTo(yearMonth);
        assertThat(res.getCalendarId()).isNotBlank();
    }

    @Test
    @DisplayName("create: duplicate (userId, yearMonth) throws DomainException")
    void create_duplicate_throws() {
        // given
        String yearMonth = "2025-10";
        
        // when
        calendarAppService.ensureExists(CalendarRequest.builder()
        		.userId(userId)
        		.yearMonth(yearMonth)
        		.build());
        
        // then
        assertThatThrownBy(() -> calendarAppService.create(CalendarRequest.builder()
        		.userId(userId)
        		.yearMonth(yearMonth)
        		.build()))
            	.isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("update: change yearMonth with conflict detection")
    void update_conflict_detection() {
        // given
        String yearMonth = "2025-10";
        String updateYearMonth = "2025-11";
        
        // when
        CalendarResponse c1 = calendarAppService.ensureExists(CalendarRequest.builder()
        		.userId(userId)
        		.yearMonth(yearMonth)
        		.build());
	    // Create another calendar for the target updateYearMonth to cause conflict
	    calendarAppService.ensureExists(CalendarRequest.builder()
	        .userId(userId)
	        .yearMonth(updateYearMonth)
	        .build());
        
        // then
        assertThatThrownBy(() -> calendarAppService.update(c1.getCalendarId(), updateYearMonth))
            .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("listByUserIdAndYearMonth: returns mealcards for month via JOIN")
    void list_month_mealcards() {
        String ym = "2025-10";
        CalendarResponse cal = calendarAppService.ensureExists(CalendarRequest.builder()
        		.userId(userId)
        		.yearMonth(ym)
        		.build());

        // create two mealcards on different dates/divisions
        mealCardAppService.create(userId, ym, MealCardRequest.builder()
            .calendarId(cal.getCalendarId())
            .recordDate(LocalDateTime.of(2025, 10, 2, 8, 0))
            .division("BREAKFAST")
            .build());
        mealCardAppService.create(userId, ym, MealCardRequest.builder()
            .calendarId(cal.getCalendarId())
            .recordDate(LocalDateTime.of(2025, 10, 2, 12, 0))
            .division("LUNCH")
            .build());

        List<MealCardResponse> list = mealCardAppService.listByUserIdAndYearMonth(userId, ym);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getRecordDate()).isBefore(list.get(1).getRecordDate());
    }
}
