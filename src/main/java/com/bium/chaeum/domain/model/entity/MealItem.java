package com.bium.chaeum.domain.model.entity;

import com.bium.chaeum.domain.model.vo.MealCardId;
import com.bium.chaeum.domain.model.vo.MealItemId;

import lombok.Getter;

@Getter
public class MealItem {
	private MealItemId id;
	private MealCardId mealCardId;
	private String name;
	private String ingredient;
	private Integer carbohydrate;
	private Integer protein;
	private Integer fat;
	private Integer sodium;
	private Integer calorie;

    private MealItem(MealItemId id, MealCardId mealCardId, String name, String ingredient, Integer carbohydrate, Integer protein, Integer fat, Integer sodium, Integer calorie) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (mealCardId == null) throw new IllegalArgumentException("mealCardId is required");
        if (name == null) throw new IllegalArgumentException("name is required");

        this.id = id;
        this.mealCardId = mealCardId;
        this.name = name;
        this.ingredient = (ingredient == null) ? "" : ingredient;
        this.carbohydrate = (carbohydrate == null) ? 0 : carbohydrate;
        this.protein = (protein == null) ? 0 : protein;
        this.fat = (fat == null) ? 0 : fat;
        this.sodium = (sodium == null) ? 0 : sodium;
        this.calorie = (calorie == null) ? 0 : calorie;
    }

    public static MealItem create(MealCardId mealCardId, String name, String ingredient, Integer carbohydrate, Integer protein, Integer fat, Integer sodium, Integer calorie){
        return new MealItem(MealItemId.newId(), mealCardId, name, ingredient, carbohydrate, protein, fat, sodium, calorie);
    }

    // 인프라 복원용(레코드 → 도메인).
    public static MealItem reconstruct(MealItemId id, MealCardId mealCardId, String name, String ingredient, Integer carbohydrate, Integer protein, Integer fat, Integer sodium, Integer calorie) {
        return new MealItem(id, mealCardId, name, ingredient, carbohydrate, protein, fat, sodium, calorie);
    }
}
