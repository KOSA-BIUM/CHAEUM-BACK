package com.bium.chaeum.infrastructure.mybatis.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.repository.MealItemRepository;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.MealItemId;
import com.bium.chaeum.infrastructure.mybatis.mapper.MealItemMapper;
import com.bium.chaeum.infrastructure.mybatis.record.MealItemRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisMealItemRepository implements MealItemRepository {

    private final MealItemMapper mapper;

    @Override
    public Optional<MealItem> findByMealItemId(MealItemId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        MealItemRecord r = mapper.selectByMealItemId(id.value());
        return Optional.ofNullable(r).map(this::toEntity);
    }

    @Override
    public List<MealItem> findByMealCardId(MealCardId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        return mapper.selectByMealCardId(id.value()).stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public void save(MealItem mealItem) {
        if (mealItem == null) throw new IllegalArgumentException("mealItem is required");
        String id = mealItem.getId().value();
        MealItemRecord record = toRecord(mealItem);
        MealItemRecord existing = mapper.selectByMealItemId(id);
        if (existing == null) {
            mapper.insert(record);
        } else {
            mapper.update(record);
        }
    }

    @Override
    public void delete(MealItemId id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        mapper.delete(id.value());
    }

    private MealItem toEntity(MealItemRecord r) {
        return MealItem.reconstruct(
            MealItemId.of(r.getMealItemId()),
            MealCardId.of(r.getMealCardId()),
            r.getName(),
            r.getIngredient(),
            toPrimitive(r.getCarbohydrate()),
            toPrimitive(r.getProtein()),
            toPrimitive(r.getFat()),
            toPrimitive(r.getSodium()),
            toPrimitive(r.getCalorie())
        );
    }

    private MealItemRecord toRecord(MealItem e) {
        return MealItemRecord.builder()
            .mealItemId(e.getId().value())
            .mealCardId(e.getMealCardId().value())
            .name(e.getName())
            .ingredient(e.getIngredient())
            .carbohydrate(e.getCarbohydrate())
            .protein(e.getProtein())
            .fat(e.getFat())
            .sodium(e.getSodium())
            .calorie(e.getCalorie())
            .build();
    }

    private Integer toPrimitive(Integer v) {
        return v == null ? 0 : v;
    }
}
