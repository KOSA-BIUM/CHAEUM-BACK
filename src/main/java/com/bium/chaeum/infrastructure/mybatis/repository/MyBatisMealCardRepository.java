package com.bium.chaeum.infrastructure.mybatis.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.MealCard;
import com.bium.chaeum.domain.model.entity.MealCardType;
import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.repository.MealCardRepository;
import com.bium.chaeum.domain.model.vo.CalendarId;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.infrastructure.mybatis.mapper.MealCardMapper;
import com.bium.chaeum.infrastructure.mybatis.record.MealCardRecord;
import com.bium.chaeum.infrastructure.mybatis.record.MealItemRecord;

import lombok.RequiredArgsConstructor;

// MyBatisMealCardRepository는 MealCard 엔티티에 대한 MyBatis 기반의 리포지토리 구현체입니다. (author: 나규태 + ChatGPT)
@Repository
@RequiredArgsConstructor
public class MyBatisMealCardRepository implements MealCardRepository {

    private final MealCardMapper mapper;

    // MealCardId로 MealCard를 찾습니다.
    @Override
    public Optional<MealCard> findByMealCardId(MealCardId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        MealCardRecord r = mapper.selectByMealCardId(id.value());
        return Optional.ofNullable(r).map(this::toEntity);
    }

    // CalendarId와 recordDate로 MealCard 목록을 찾습니다.
    @Override
    public List<MealCard> findListByRecordDate(CalendarId calendarId, LocalDateTime recordDate) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        return mapper.selectByRecordDate(calendarId.value(), recordDate).stream().map(this::toEntity).collect(Collectors.toList());
    }

    // CalendarId로 MealCard 목록을 찾습니다.
    @Override
    public List<MealCard> findListByCalendarId(CalendarId calendarId) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        return mapper.selectByCalendarId(calendarId.value()).stream().map(this::toEntity).collect(Collectors.toList());
    }

    // CalendarId, recordDate, division으로 MealCard를 찾습니다.
    @Override
    public Optional<MealCard> findByCalendarIdAndRecordDateAndDivision(CalendarId calendarId, LocalDateTime recordDate, String division) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        if (division == null || division.isBlank()) throw new IllegalArgumentException("division is required");
        MealCardRecord r = mapper.selectByCalendarIdAndRecordDateAndDivision(calendarId.value(), recordDate, division);
        return Optional.ofNullable(r).map(this::toEntity);
    }
    
    // 기간으로 MealCard 목록을 찾습니다.
    @Override
    public List<MealCard> findListByPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null) throw new IllegalArgumentException("start is required");
        if (end == null) throw new IllegalArgumentException("end is required");
        return mapper.selectByPeriod(start,end).stream().map(this::toEntity).collect(Collectors.toList());
    }

    // MealCard를 저장합니다.
    @Override
    public void save(MealCard mealCard) {
        if (mealCard == null) throw new IllegalArgumentException("mealCard is required");
        MealCardRecord existing = mapper.selectByMealCardId(mealCard.getId().value());
        MealCardRecord record = toRecord(mealCard);
        if (existing == null) {
            mapper.insert(record);
        } else {
            mapper.update(record);
        }
    }

    // MealCard를 삭제합니다.
    @Override
    public void delete(MealCardId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        mapper.delete(id.value());
    }

    // Mapping helpers
    private MealCard toEntity(MealCardRecord r) {
        List<MealItem> items = new ArrayList<>();
        if (r.getMealItems() != null) {
            for (MealItemRecord mir : r.getMealItems()) {
                if (mir == null || mir.getMealItemId() == null) continue;
                items.add(MealItem.reconstruct(
                    com.bium.chaeum.domain.model.vo.MealItemId.of(mir.getMealItemId()),
                    MealCardId.of(mir.getMealCardId() == null ? r.getMealCardId() : mir.getMealCardId()),
                    mir.getName(),
                    mir.getIngredient(),
                    toPrimitive(mir.getCarbohydrate()),
                    toPrimitive(mir.getProtein()),
                    toPrimitive(mir.getFat()),
                    toPrimitive(mir.getSodium()),
                    toPrimitive(mir.getCalorie())
                ));
            }
        }
        return MealCard.reconstruct(
            MealCardId.of(r.getMealCardId()),
            CalendarId.of(r.getCalendarId()),
            r.getRecordDate(),
            r.getDivision() == null ? null : MealCardType.valueOf(r.getDivision()),
            items
        );
    }

    // MealCard 엔티티를 MealCardRecord로 매핑
    private MealCardRecord toRecord(MealCard e) {
        return MealCardRecord.builder()
            .mealCardId(e.getId().value())
            .calendarId(e.getCalendarId().value())
            .recordDate(e.getRecordDate())
            .division(e.getDivision() == null ? null : e.getDivision().name())
            .mealItems(null)
            .build();
    }

    private Integer toPrimitive(Integer v) {
        return v == null ? 0 : v;
    }
}
