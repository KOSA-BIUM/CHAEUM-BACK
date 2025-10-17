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

@Repository
@RequiredArgsConstructor
public class MyBatisMealCardRepository implements MealCardRepository {

    private final MealCardMapper mapper;

    @Override
    public Optional<MealCard> findByMealCardId(MealCardId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        MealCardRecord r = mapper.selectByMealCardId(id.value());
        return Optional.ofNullable(r).map(this::toEntity);
    }

    @Override
    public List<MealCard> findListByRecordDate(CalendarId calendarId, LocalDateTime recordDate) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        return mapper.selectByRecordDate(calendarId.value(), recordDate).stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<MealCard> findListByCalendarId(CalendarId calendarId) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        return mapper.selectByCalendarId(calendarId.value()).stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<MealCard> findByCalendarIdAndRecordDateAndDivision(CalendarId calendarId, LocalDateTime recordDate, String division) {
        if (calendarId == null) throw new IllegalArgumentException("calendarId is required");
        if (recordDate == null) throw new IllegalArgumentException("recordDate is required");
        if (division == null || division.isBlank()) throw new IllegalArgumentException("division is required");
        MealCardRecord r = mapper.selectByCalendarIdAndRecordDateAndDivision(calendarId.value(), recordDate, division);
        return Optional.ofNullable(r).map(this::toEntity);
    }

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

    @Override
    public void delete(MealCardId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        mapper.delete(id.value());
    }

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
