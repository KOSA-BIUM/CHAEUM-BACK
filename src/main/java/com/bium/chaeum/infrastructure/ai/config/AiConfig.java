package com.bium.chaeum.infrastructure.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bium.chaeum.domain.model.vo.AiWeeklyMealItem;


/**
 * author: 이상우
 * Spring AI의 핵심 컴포넌트인 ChatClient 및
 * 구조화된 JSON 응답을 위한 OutputConverter를 Spring 컨테이너에 Bean으로 등록하는 설정 클래스입니다.
 * 이 설정은 인프라 계층(Adapter)에서 AI 통신을 수행할 때 필요한 의존성을 제공합니다.
 */
@Configuration
public class AiConfig {
	/**
     * WeeklyMeal DTO를 위한 BeanOutputConverter를 등록합니다.
     * 역할:
     * 1. WeeklyMeal DTO 구조를 분석하여 OpenAI에 전달할 JSON 스키마를 생성합니다.
     * 2. OpenAI로부터 받은 JSON 응답을 WeeklyMeal Java 객체로 변환(파싱)하는 역할을 수행합니다.
     * * @return WeeklyMeal 객체로 변환을 담당하는 BeanOutputConverter 인스턴스
     */
	@Bean
	BeanOutputConverter<AiWeeklyMealItem> weeklyMealConverter() {
		return new BeanOutputConverter<>(AiWeeklyMealItem.class);
	}

	/**
     * Spring AI 통신의 핵심 인터페이스인 ChatClient를 등록합니다.
     * Spring Boot의 자동 구성에 의해 ChatClient.Builder가 주입됩니다.
     * 이 빌더는 이미 OpenAI 모델과 API 키 설정을 포함하고 있습니다.
     * 역할:
     * 1. 실제 AI 모델(GPT 등)에 프롬프트를 전송하고 응답을 받는 통신을 추상화합니다.
     * 2. 인프라 계층(OpenAiAdapter)에서 AI 호출을 간편하게 수행할 수 있도록 합니다.
     * @param builder Spring AI에 의해 자동 구성된 ChatClient.Builder
     * @return 최종적으로 빌드된 ChatClient 인스턴스
     */
	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}
}