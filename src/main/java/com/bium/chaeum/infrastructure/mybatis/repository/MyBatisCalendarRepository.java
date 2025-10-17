package com.bium.chaeum.infrastructure.mybatis.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.Calendar;
import com.bium.chaeum.domain.model.entity.MealCard;
import com.bium.chaeum.domain.model.entity.MealCardType;
import com.bium.chaeum.domain.model.repository.CalendarRepository;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.UserId;
import com.bium.chaeum.infrastructure.mybatis.mapper.CalendarMapper;
import com.bium.chaeum.infrastructure.mybatis.record.CalendarRecord;
import com.bium.chaeum.infrastructure.mybatis.record.MealCardRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisCalendarRepository implements CalendarRepository {

    private final CalendarMapper mapper;

    @Override
    public Optional<Calendar> findByCalendarId(CalendarId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        CalendarRecord r = mapper.selectByCalendarId(id.value());
        return Optional.ofNullable(r).map(this::toEntityShallow);
    }

    @Override
    public Optional<Calendar> findByUserId(UserId userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        CalendarRecord r = mapper.selectByUserId(userId.value());
        return Optional.ofNullable(r).map(this::toEntityShallow);
    }

    @Override
    public Optional<Calendar> findByUserIdAndYearMonth(UserId userId, String yearMonth) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (yearMonth == null) throw new IllegalArgumentException("yearMonth is required");
        // Prefer the JOINed variant to also populate mealCards when present
        CalendarRecord r = mapper.selectWithMealCardsByUserIdAndYearMonth(userId.value(), yearMonth);
        if (r == null) return Optional.empty();
        return Optional.of(toEntityWithMealCards(r));
    }

    @Override
    public void save(Calendar calendar) {
        if (calendar == null) throw new IllegalArgumentException("calendar is required");
        CalendarRecord existing = mapper.selectByCalendarId(calendar.getId().value());
        CalendarRecord record = toRecord(calendar);
        if (existing == null) {
            mapper.insert(record);
        } else {
            mapper.update(record);
        }
    }

    // Mapping helpers
    private Calendar toEntityShallow(CalendarRecord r) {
        return Calendar.reconstruct(
            CalendarId.of(r.getCalendarId()),
            UserId.of(r.getUserId()),
            r.getYearMonth(),
            Collections.emptyList()
        );
    }

    private Calendar toEntityWithMealCards(CalendarRecord r) {
        List<MealCard> cards = new ArrayList<>();
        if (r.getMealCards() != null) {
            for (MealCardRecord mcr : r.getMealCards()) {
                if (mcr == null || mcr.getMealCardId() == null) continue;
                cards.add(MealCard.reconstruct(
                    MealCardId.of(mcr.getMealCardId()),
                    CalendarId.of(Objects.requireNonNullElse(mcr.getCalendarId(), r.getCalendarId())),
                    mcr.getRecordDate(),
                    mcr.getDivision() == null ? null : MealCardType.valueOf(mcr.getDivision()),
                    // meal items may be populated by another mapper; default to empty here
                    Collections.emptyList()
                ));
            }
        }
        return Calendar.reconstruct(
            CalendarId.of(r.getCalendarId()),
            UserId.of(r.getUserId()),
            r.getYearMonth(),
            cards
        );
    }

    private CalendarRecord toRecord(Calendar e) {
        List<MealCardRecord> mealCardRecords = null; // Calendar insert/update doesn't cascade mealcards here
        return CalendarRecord.builder()
            .calendarId(e.getId().value())
            .userId(e.getUserId().value())
            .yearMonth(e.getYearMonth())
            .mealCards(mealCardRecords)
            .build();
    }
}
