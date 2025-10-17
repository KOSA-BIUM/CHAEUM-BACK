package com.bium.chaeum.infrastructure.ai.adapter;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Component;

import com.bium.chaeum.domain.model.vo.AiWeeklyMealItem;



/**
 * author: 이상우
 * OpenAI API와의 통신을 전담하는 인프라 계층의 어댑터(Adapter) 클래스입니다.
 * ChatClient를 사용하여 AI 모델에 요청을 보내고, 응답을 WeeklyMeal DTO 객체로 변환하는 역할을 수행합니다.
 */
@Component
public class OpenAiAdapter {
    
	// AI 통신을 위한 핵심 인터페이스 (Spring AI에 의해 주입)
    private final ChatClient chatClient;
    
    // AI 응답 파싱 및 스키마 생성을 위한 컨버터
    // WeeklyMeal 클래스를 기반으로 인스턴스를 생성
    private final BeanOutputConverter<AiWeeklyMealItem> weeklyMealConverter;
    
    /**
     * 생성자 주입을 통한 의존성 설정.
     * @param chatClient AI 통신 인터페이스
     * @param weeklyMealConverter WeeklyMeal 객체 변환 및 스키마 생성을 위한 컨버터
     */
    public OpenAiAdapter(ChatClient chatClient, BeanOutputConverter<AiWeeklyMealItem> weeklyMealConverter) {
        this.chatClient = chatClient;
        this.weeklyMealConverter = weeklyMealConverter;
    }

    /**
     * AI 모델에 식단 추천 요청을 보내고, 구조화된 응답을 WeeklyMeal 객체로 반환합니다.
     * @param systemPrompt AI의 역할 및 제약 조건을 정의한 시스템 프롬프트
     * @param userPrompt 사용자 데이터 및 최종 요청 내용을 담은 사용자 프롬프트
     * @return AI가 생성한 식단 정보 WeeklyMeal 객체
     * @throws RuntimeException 유효한 JSON을 반환하지 않거나 파싱에 실패했을 경우 발생
     */
    public AiWeeklyMealItem generateWeeklyDiet(String systemPrompt, String userPrompt) {
        // DTO를 분석하여 JSON 스키마 문자열을 얻습니다. (Schema Generation)
        String jsonSchema = weeklyMealConverter.getJsonSchema();
        
        // chatOptions 변수 선언 및 설정
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .temperature(0.7)
            .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
            .build();
        
        // AI 호출 및 응답 받기
        ChatResponse response = chatClient.prompt()
        		.system(systemPrompt)
        		.user(userPrompt)
        		.options(chatOptions)
        		.call()
        		.chatResponse();
        
        // 응답 텍스트(JSON 문자열) 추출
        String jsonString = response.getResult().getOutput().getText();
        
        // AI가 스키마를 무시하고 일반 텍스트나 null을 반환했을 경우를 대비한 검증입니다.
        if (jsonString == null || !jsonString.trim().startsWith("{")) {
            // 예외를 던져 서비스 계층에서 적절히 처리하도록 합니다.
            throw new RuntimeException("AI가 유효한 JSON을 반환하지 않았습니다: " + jsonString);
        }

        try {
        	// 응답 JSON 문자열을 WeeklyMeal 객체로 파싱합니다. (Parsing)
        	return weeklyMealConverter.convert(jsonString);
        } catch (Exception e) {
            // JSON 파싱 중 오류 발생 시 예외 처리
            throw new RuntimeException("AI 응답을 WeeklyMeal 객체로 파싱하는 데 실패했습니다.", e);
        }
    }
}