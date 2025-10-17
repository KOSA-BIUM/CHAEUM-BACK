package com.bium.chaeum.domain.model.entity;

import com.bium.chaeum.domain.model.vo.UserId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
public class Profile {

    private UserId userId;
    private LocalDate birthDate;
    private GenderType gender;
    private Integer height;
    private Integer weight;
    private PreferredDietType preferredDiet;
    private Long bmr;   // 기초대사량

    private Profile(UserId userId, LocalDate birthDate, GenderType gender,
                    Integer height, Integer weight, PreferredDietType preferredDiet, Long bmr) {

        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (birthDate == null) throw new IllegalArgumentException("birthDate is required");
        if (gender == null) throw new IllegalArgumentException("gender is required");
        if (height == null) throw new IllegalArgumentException("height is required");
        if (weight == null) throw new IllegalArgumentException("weight is required");

        this.userId = userId;
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.preferredDiet = preferredDiet;
        this.bmr = (bmr != null) ? bmr : calculateBmr();
    }

    public static Profile create(UserId userId, LocalDate birthDate, GenderType gender,
                             Integer height, Integer weight, PreferredDietType preferredDiet) {
        return new Profile(userId, birthDate, gender, height, weight, preferredDiet, null);
    }

    public static Profile reconstruct(UserId userId, LocalDate birthDate, GenderType gender,
                                      Integer height, Integer weight, PreferredDietType preferredDiet, Long bmr) {

        return new Profile(userId, birthDate, gender, height, weight, preferredDiet, bmr);
    }

    // 프로필 수정
    public void updateProfile(Integer height, Integer weight,
                              PreferredDietType preferredDiet) {

        if (height == null) throw new IllegalArgumentException("height is required");
        if (weight == null) throw new IllegalArgumentException("weight is required");
        if (preferredDiet == null) throw new IllegalArgumentException("preferredDiet is required");

        this.height = height;
        this.weight = weight;
        this.preferredDiet = preferredDiet;
        this.bmr = calculateBmr();
    }

    // 기초대사량 계산.
    private Long calculateBmr() {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        double bmrValue;

        if (gender == GenderType.MALE) {
            bmrValue = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmrValue = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        return Math.round(bmrValue);
    }
}
