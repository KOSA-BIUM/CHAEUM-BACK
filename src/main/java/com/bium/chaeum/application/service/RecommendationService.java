package com.bium.chaeum.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bium.chaeum.application.response.RecommendationResponse;
import com.bium.chaeum.domain.model.entity.Recommendation;
import com.bium.chaeum.domain.model.entity.RecommendationMealItem;
import com.bium.chaeum.domain.model.entity.User;
import com.bium.chaeum.domain.model.repository.RecommendationMealItemRepository;
import com.bium.chaeum.domain.model.repository.RecommendationRepository;
import com.bium.chaeum.domain.model.repository.UserRepository;
import com.bium.chaeum.domain.model.vo.AiWeeklyMealItem;
import com.bium.chaeum.infrastructure.ai.adapter.OpenAiAdapter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
	
	private final OpenAiAdapter openAiAdapter;
	private final RecommendationRepository recommendationRepository;
	private final RecommendationMealItemRepository recommendationMealItemRepository;
	private final UserRepository userRepository;

	@Transactional
    public RecommendationResponse executeRecommendation() {
        
        // 1. 필요한 데이터 수집 및 프롬프트 문자열 생성
        String userPrompt = """
		[사용자 프로필 및 목표]
		- 성별 및 연령: {USER.GENDER}, {USER.BIRTH}세 (예: FEMALE, 30세)
		- 현재 신체 정보: 키 {PROFILE.HEIGHT}cm, 체중 {PROFILE.WEIGHT}kg
		- 기초대사량 (BMR): {PROFILE.BMR} Kcal
		- 선호 식단: {PROFILE.PREFERRED_DIET} (예: 저탄고지, 비건)
		
		[과거 식단 분석 (지난 {N}일 기준)]
		- 과거 식단: 과거 식단 데이터(List)
		
		출력 요청 (Final Request)
		위의 모든 제약 조건과 목표를 반영하여, 오늘부터 7일간의 식단표를 JSON 스키마에 맞춰 생성하세요.
		        		""";
        // 2. 인프라(AI 어댑터)를 통해 AI 호출 및 파싱된 객체 받기
        AiWeeklyMealItem mealPlan = openAiAdapter.generateWeeklyDiet(SYSTEM_PROMPT, userPrompt);
        
        // 3. DB 저장 및 트랜잭션 로직 수행...
        User user = User.create("test@gmail.com", "test", "test");
        userRepository.save(user);
        Recommendation recommendation = Recommendation.create(user.getId(), userPrompt, mealPlan.recommendationReason());
        recommendationRepository.save(recommendation);
        for(int i = 0; i < mealPlan.meals().size(); i++) {
        	RecommendationMealItem recommendationMealItem = RecommendationMealItem.create(recommendation.getId(), mealPlan.meals().get(i));
            recommendationMealItemRepository.save(recommendationMealItem);
        }
        return RecommendationResponse.from(mealPlan);
    }
    
    private static final String SYSTEM_PROMPT = """
    		당신은 개인화된 식단을 전문으로 하는 영양사 AI입니다. 당신의 목표는 사용자에게 최적화된 7일치 식단을 추천하는 것입니다.
		
			[출력 형식 제약]
			1. 당신의 답변은 반드시 첨부된 JSON 스키마(JSON_SCHEMA)를 완벽하게 준수하는 JSON 객체여야 합니다.
			2. 어떠한 설명, 서론, 주석, 마크다운 코드 블록(```json) 없이 순수한 JSON 객체만 반환해야 합니다.
			3. 식단 항목은 1일차 아침부터 7일차 저녁까지 빠짐없이 채워져야 합니다.
			4. 식단 추천 이유는 한국어로 답변해야 합니다.
			
			[주요 임무]
			1. 사용자의 신체와 과거 식단 분석 결과를 반영하여 식단을 구성합니다.
			2. 모든 식단 항목은 기초대사량 기준에 맞도록 세밀하게 칼로리 및 영양소를 배분해야 합니다.
			
			[제약 조건 및 금지 사항]
    		- 중복 제한: 주간 식단에서 동일한 메인 메뉴(foodName)는 2회까지만 허용합니다.
    		""";
}