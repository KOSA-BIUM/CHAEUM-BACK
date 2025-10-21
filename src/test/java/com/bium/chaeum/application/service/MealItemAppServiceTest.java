package com.bium.chaeum.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.request.CalendarRequest;
import com.bium.chaeum.application.request.MealCardWithItemsRequest;
import com.bium.chaeum.application.request.MealItemRequest;
import com.bium.chaeum.application.request.SignUpRequest;
import com.bium.chaeum.application.response.CalendarResponse;
import com.bium.chaeum.application.response.MealCardResponse;
import com.bium.chaeum.application.response.MealItemResponse;
import com.bium.chaeum.application.response.UserResponse;
import com.bium.chaeum.domain.model.repository.MealItemRepository;
import com.bium.chaeum.domain.model.vo.MealItemId;

// MealItemAppServiceTest는 MealItemAppService의 주요 기능들을 테스트합니다. (author: 나규태 + ChatGPT)
@SpringBootTest
@Transactional
class MealItemAppServiceTest {

    @Autowired
    private MealItemAppService mealItemAppService;
    @Autowired
    private MealItemRepository mealItemRepository;
    @Autowired
    private CalendarAppService calendarAppService;
    @Autowired
    private MealCardAppService mealCardAppService;
    @Autowired
    private UserAppService userAppService;
    
    private String userId;
    private String calendarId;
    private String mealCardId;
    private final String yearMonth = "2025-10";

    // 테스트를 위한 사용자 및 캘린더 설정
    @BeforeEach
    void setUpUser() {
        String email = "test+" + java.util.UUID.randomUUID() + "@local";
        String password = "Secret123!";
        String name = "Tester";
        LocalDateTime recordDate = LocalDateTime.of(2025, 10, 2, 8, 0);
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
        
        MealCardResponse mcr = mealCardAppService.createWithItemsByCalendarId(calendarId, MealCardWithItemsRequest.builder()
            	.calendarId(calendarId)            
                .recordDate(recordDate)
                .division("BREAKFAST")
                .items(List.of())
                .build());
        this.mealCardId = mcr.getMealCardId();
    }

    // 식사 항목 생성 테스트
    @Test
    @DisplayName("create: valid request → save called and response mapped")
    void create_success() {
        // given
        MealItemRequest req = MealItemRequest.builder()
                .mealCardId(mealCardId)
                .name("Apple")
                .ingredient("Fruit")
                .carbohydrate(25)
                .protein(0)
                .fat(0)
                .sodium(1)
                .calorie(95)
                .build();

        // when
        MealItemResponse res = mealItemAppService.create(req);
        System.out.println(res);
        // then
        assertThat(res.getMealItemId()).isNotBlank();
        assertThat(res.getMealCardId()).isEqualTo(mealCardId);
        assertThat(res.getName()).isEqualTo("Apple");
        assertThat(res.getCalorie()).isEqualTo(95);

	    // and persisted (via repository)
	    assertThat(mealItemRepository.findByMealItemId(MealItemId.of(res.getMealItemId()))).isPresent();
    }

    // 식사 항목 업데이트 테스트
    @Test
    @DisplayName("update: merge nulls → keep existing values, save called")
    void update_merge_success() {
        // given: create existing entity first
        MealItemResponse created = mealItemAppService.create(MealItemRequest.builder()
                .mealCardId(mealCardId)
                .name("Banana")
                .ingredient("Fruit")
                .carbohydrate(27)
                .protein(1)
                .fat(0)
                .sodium(1)
                .calorie(105)
                .build());

        String mealItemId = created.getMealItemId();

    // when: change only ingredient and calorie (others null → keep)
        MealItemRequest patch = MealItemRequest.builder()
                .ingredient("Fresh Fruit")
                .calorie(100)
                .build();

        MealItemResponse res = mealItemAppService.update(mealItemId, patch);

        // then
        assertThat(res.getName()).isEqualTo("Banana"); // unchanged
        assertThat(res.getIngredient()).isEqualTo("Fresh Fruit");
        assertThat(res.getCalorie()).isEqualTo(100);
    }

    // 식사 항목 삭제 테스트
    @Test
    @DisplayName("delete: calls repository.delete")
    void delete_calls_repo() {
    // given: create then delete
    MealItemResponse created = mealItemAppService.create(MealItemRequest.builder()
        .mealCardId(mealCardId)
        .name("Temp")
        .calorie(1)
        .build());

    mealItemAppService.delete(created.getMealItemId());

    assertThat(
        mealItemRepository.findByMealItemId(MealItemId.of(created.getMealItemId()))
    ).isNotPresent();
    }

    // 식사 항목 조회 테스트
    @Test
    @DisplayName("getByMealItemId: present → mapped to response")
    void get_present() {
    MealItemResponse created = mealItemAppService.create(MealItemRequest.builder()
        .mealCardId(mealCardId)
        .name("Milk")
        .ingredient("Dairy")
        .carbohydrate(12)
        .protein(6)
        .fat(8)
        .sodium(120)
        .calorie(150)
        .build());

    Optional<MealItemResponse> got = mealItemAppService.getByMealItemId(created.getMealItemId());
        assertThat(got).isPresent();
        assertThat(got.get().getName()).isEqualTo("Milk");
    }

    // 식사 카드 ID로 식사 항목 목록 조회 테스트
    @Test
    @DisplayName("listByMealCardId: returns mapped list")
    void list_by_mealcard() {
        mealItemAppService.create(MealItemRequest.builder().mealCardId(mealCardId).name("Egg").ingredient("Protein").carbohydrate(1).protein(6).fat(5).sodium(62).calorie(78).build());
        mealItemAppService.create(MealItemRequest.builder().mealCardId(mealCardId).name("Toast").ingredient("Bread").carbohydrate(13).protein(3).fat(1).sodium(160).calorie(75).build());

        List<MealItemResponse> list = mealItemAppService.listByMealCardId(mealCardId);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo("Egg");
        assertThat(list.get(1).getName()).isEqualTo("Toast");
    }
}
