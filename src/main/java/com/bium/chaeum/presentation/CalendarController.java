package com.bium.chaeum.presentation;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bium.chaeum.application.response.CalendarResponse;
import com.bium.chaeum.application.service.CalendarAppService;
import com.bium.chaeum.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

// CalendarController는 캘린더 관련 API 엔드포인트를 처리합니다. (author: 나규태 + ChatGPT)
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarAppService calendarAppService;

    // GET /api/calendars?userId=...&yearMonth=YYYY-MM
    @GetMapping
    public ResponseEntity<CalendarResponse> getByUserAndMonth(
        @RequestParam("userId") String userId,
        @RequestParam("yearMonth") String yearMonth
    ) {
        Optional<CalendarResponse> res = calendarAppService.getByUserIdAndYearMonth(userId, yearMonth);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Basic error mapping
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Domain error mapping
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainConflict(DomainException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
