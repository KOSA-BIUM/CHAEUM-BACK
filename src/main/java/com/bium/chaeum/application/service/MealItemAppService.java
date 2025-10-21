package com.bium.chaeum.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.request.MealItemRequest;
import com.bium.chaeum.application.response.MealItemResponse;
import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.repository.MealItemRepository;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.MealItemId;

import lombok.RequiredArgsConstructor;

// MealItemAppService는 식사 항목(MealItem)에 대한 생성, 수정, 삭제, 조회 기능을 제공합니다. (author: 나규태 + ChatGPT)
@Service
@RequiredArgsConstructor
public class MealItemAppService {

    private final MealItemRepository mealItemRepository;

    // 식사 항목 생성
    @Transactional
    public MealItemResponse create(MealItemRequest request) {
        if (request == null) throw new IllegalArgumentException("request is required");
        if (request.getMealCardId() == null) throw new IllegalArgumentException("mealCardId is required");
        if (request.getName() == null || request.getName().isBlank()) throw new IllegalArgumentException("name is required");

        MealItem item = MealItem.create(
            MealCardId.of(request.getMealCardId()),
            request.getName(),
            request.getIngredient(),
            toPrimitive(request.getCarbohydrate()),
            toPrimitive(request.getProtein()),
            toPrimitive(request.getFat()),
            toPrimitive(request.getSodium()),
            toPrimitive(request.getCalorie())
        );
        mealItemRepository.save(item);
        return MealItemResponse.from(item);
    }

    // 식사 항목 수정
    @Transactional
    public MealItemResponse update(String mealItemId, MealItemRequest request) {
        if (mealItemId == null) throw new IllegalArgumentException("mealItemId is required");
        if (request == null) throw new IllegalArgumentException("request is required");

        MealItem existing = mealItemRepository.findByMealItemId(MealItemId.of(mealItemId))
            .orElseThrow(() -> new IllegalArgumentException("MealItem not found: " + mealItemId));

        // Merge: request 값이 null이면 기존 값 유지
        String name = (request.getName() == null || request.getName().isBlank()) ? existing.getName() : request.getName();
        String ingredient = (request.getIngredient() == null) ? existing.getIngredient() : request.getIngredient();
        Integer carbohydrate = (request.getCarbohydrate() == null) ? existing.getCarbohydrate() : request.getCarbohydrate();
        Integer protein = (request.getProtein() == null) ? existing.getProtein() : request.getProtein();
        Integer fat = (request.getFat() == null) ? existing.getFat() : request.getFat();
        Integer sodium = (request.getSodium() == null) ? existing.getSodium() : request.getSodium();
        Integer calorie = (request.getCalorie() == null) ? existing.getCalorie() : request.getCalorie();

        MealItem merged = MealItem.reconstruct(
            existing.getId(),
            (request.getMealCardId() == null ? existing.getMealCardId() : MealCardId.of(request.getMealCardId())),
            name,
            ingredient,
            carbohydrate,
            protein,
            fat,
            sodium,
            calorie
        );
        mealItemRepository.save(merged);
        return MealItemResponse.from(merged);
    }

    // 식사 항목 삭제
    @Transactional
    public void delete(String mealItemId) {
        if (mealItemId == null) throw new IllegalArgumentException("mealItemId is required");
        mealItemRepository.delete(MealItemId.of(mealItemId));
    }

    // 식사 항목 단건 조회
    @Transactional(readOnly = true)
    public Optional<MealItemResponse> getByMealItemId(String mealItemId) {
        if (mealItemId == null) throw new IllegalArgumentException("mealItemId is required");
        return mealItemRepository.findByMealItemId(MealItemId.of(mealItemId)).map(MealItemResponse::from);
    }

    // 특정 MealCard에 속한 식사 항목 목록 조회
    @Transactional(readOnly = true)
    public List<MealItemResponse> listByMealCardId(String mealCardId) {
        if (mealCardId == null) throw new IllegalArgumentException("mealCardId is required");
        return mealItemRepository.findByMealCardId(MealCardId.of(mealCardId))
                .stream()
                .map(MealItemResponse::from)
                .collect(Collectors.toList());
    }

    // null 안전한 Integer 변환 헬퍼
    private Integer toPrimitive(Integer v) {
        return v == null ? 0 : v;
    }
}
