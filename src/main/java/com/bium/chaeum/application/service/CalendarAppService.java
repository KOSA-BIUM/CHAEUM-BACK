package com.bium.chaeum.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.request.CalendarRequest;
import com.bium.chaeum.application.response.CalendarResponse;
import com.bium.chaeum.domain.model.entity.Calendar;
import com.bium.chaeum.domain.model.repository.CalendarRepository;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarAppService {

    private final CalendarRepository calendarRepository;

    @Transactional(readOnly = true)
    public Optional<CalendarResponse> getByCalendarId(String calendarId) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        return calendarRepository.findByCalendarId(CalendarId.of(calendarId)).map(CalendarResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<CalendarResponse> getByUserId(String userId) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        return calendarRepository.findByUserId(UserId.of(userId)).map(CalendarResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<CalendarResponse> getByUserIdAndYearMonth(String userId, String yearMonth) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null || yearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");
        return calendarRepository.findByUserIdAndYearMonth(UserId.of(userId), yearMonth).map(CalendarResponse::from);
    }

    @Transactional
    public CalendarResponse createOrUpdate(CalendarRequest request) {
        // Backward-compatible wrapper. Prefer ensureExists() for intent clarity.
        return ensureExists(request);
    }

    @Transactional
    public CalendarResponse ensureExists(CalendarRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getUserId() == null || request.getUserId().isBlank()) throw new IllegalArgumentException("userId is required");
        if (request.getYearMonth() == null || request.getYearMonth().isBlank()) throw new IllegalArgumentException("yearMonth is required");

        Optional<Calendar> existing = calendarRepository.findByUserIdAndYearMonth(UserId.of(request.getUserId()), request.getYearMonth());
        Calendar calendar = existing.orElseGet(() -> Calendar.create(UserId.of(request.getUserId()), request.getYearMonth(), List.of()));
        calendarRepository.save(calendar);
        return CalendarResponse.from(calendar);
    }

    @Transactional
    public CalendarResponse create(CalendarRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getUserId() == null || request.getUserId().isBlank()) throw new IllegalArgumentException("userId is required");
        if (request.getYearMonth() == null || request.getYearMonth().isBlank()) throw new IllegalArgumentException("yearMonth is required");

        // 유니크 키: (userId, yearMonth)
        Optional<Calendar> dup = calendarRepository.findByUserIdAndYearMonth(UserId.of(request.getUserId()), request.getYearMonth());
        if (dup.isPresent()) {
            throw new DomainException("Calendar already exists for user " + request.getUserId() + " and yearMonth " + request.getYearMonth());
        }

        Calendar created = Calendar.create(UserId.of(request.getUserId()), request.getYearMonth(), List.of());
        calendarRepository.save(created);
        return CalendarResponse.from(created);
    }

    @Transactional
    public CalendarResponse update(String calendarId, String newYearMonth) {
        if (calendarId == null || calendarId.isBlank()) throw new IllegalArgumentException("calendarId is required");
        if (newYearMonth == null || newYearMonth.isBlank()) throw new IllegalArgumentException("yearMonth is required");

        Calendar existing = calendarRepository.findByCalendarId(CalendarId.of(calendarId))
            .orElseThrow(() -> new DomainException("Calendar not found: " + calendarId));

        // 동일 조합 중복 체크 (다른 캘린더와 충돌 방지)
        Optional<Calendar> conflict = calendarRepository.findByUserIdAndYearMonth(existing.getUserId(), newYearMonth);
        if (conflict.isPresent() && !conflict.get().getId().value().equals(existing.getId().value())) {
            throw new DomainException("Calendar already exists for user " + existing.getUserId().value() + " and yearMonth " + newYearMonth);
        }

        Calendar updated = Calendar.reconstruct(existing.getId(), existing.getUserId(), newYearMonth, existing.getMealCards());
        calendarRepository.save(updated);
        return CalendarResponse.from(updated);
    }
}
