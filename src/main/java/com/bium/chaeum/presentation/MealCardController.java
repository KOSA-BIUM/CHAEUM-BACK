package com.bium.chaeum.presentation;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bium.chaeum.application.request.MealCardRequest;
import com.bium.chaeum.application.request.MealCardWithItemsRequest;
import com.bium.chaeum.application.response.MealCardResponse;
import com.bium.chaeum.application.service.MealCardAppService;
import com.bium.chaeum.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

// MealCardController는 식사 기록 카드 관련 API 엔드포인트를 처리합니다. (author: 나규태 + ChatGPT)
@RestController
@RequestMapping("/api/mealCard")
@RequiredArgsConstructor
public class MealCardController {

    private final MealCardAppService mealCardAppService;

    // List by calendarId (monthly view uses calendarId)
    @GetMapping
    public ResponseEntity<List<MealCardResponse>> listByCalendarId(@RequestParam("calendarId") String calendarId) {
        List<MealCardResponse> list = mealCardAppService.listByCalendarId(calendarId);
        return ResponseEntity.ok(list);
    }

    // Get detail by mealCardId
    @GetMapping("/{mealCardId}")
    public ResponseEntity<MealCardResponse> getMealCardDetail(@PathVariable("mealCardId") String mealCardId) {
        Optional<MealCardResponse> res = mealCardAppService.getDetailByMealCardId(mealCardId);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a meal card (and items).
    @PostMapping
    public ResponseEntity<MealCardResponse> create(@RequestBody MealCardWithItemsRequest request) {
        MealCardResponse created;
        if (request.getCalendarId() != null && !request.getCalendarId().isBlank()) {
            created = mealCardAppService.createWithItemsByCalendarId(request.getCalendarId(), request);
        } else if (request.getUserId() != null && !request.getUserId().isBlank()
                && request.getYearMonth() != null && !request.getYearMonth().isBlank()) {
            created = mealCardAppService.createWithItems(request.getUserId(), request.getYearMonth(), request);
        } else {
            throw new IllegalArgumentException("Either calendarId or (userId and yearMonth) must be provided");
        }
        return ResponseEntity.created(URI.create("/api/mealCard/" + created.getMealCardId())).body(created);
    }

    // Composite update: meal card + items (full replacement of items)
    @PutMapping("/{mealCardId}")
    public ResponseEntity<MealCardResponse> updateWithItems(
        @PathVariable("mealCardId") String mealCardId,
        @RequestBody MealCardWithItemsRequest request
    ) {
        MealCardResponse updated = mealCardAppService.updateWithItems(mealCardId, request);
        return ResponseEntity.ok(updated);
    }

    // Delete a meal card
    @DeleteMapping("/{mealCardId}")
    public ResponseEntity<Void> delete(@PathVariable("mealCardId") String mealCardId) {
        mealCardAppService.delete(mealCardId);
        return ResponseEntity.noContent().build();
    }

    // Error mapping
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleConflict(DomainException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
