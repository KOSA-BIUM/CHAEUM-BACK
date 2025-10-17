package com.bium.chaeum.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.bium.chaeum.domain.model.entity.MealItem;
import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.MealItemId;

public interface MealItemRepository {
	Optional<MealItem> findByMealItemId(MealItemId id);
	List<MealItem> findByMealCardId(MealCardId id);	
	
	// UPSERT = 새 UUID면 insert, 아니면 update
	void save(MealItem mealItem);
	void delete(MealItemId id);
}
