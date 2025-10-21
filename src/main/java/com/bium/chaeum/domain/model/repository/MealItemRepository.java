package com.bium.chaeum.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.MealItemId;

// MealItemRepository는 MealItem 엔티티에 대한 저장소 인터페이스로, 식사 항목 데이터를 조회하고 저장하는 기능을 제공합니다. (author: 나규태)
public interface MealItemRepository {
	Optional<MealItem> findByMealItemId(MealItemId id);
	List<MealItem> findByMealCardId(MealCardId id);	
	
	// UPSERT = 새 UUID면 insert, 아니면 update
	void save(MealItem mealItem);
	void delete(MealItemId id);
}
