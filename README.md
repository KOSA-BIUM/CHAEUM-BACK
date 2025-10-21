# 사용자 맞춤형 AI 식단 관리 서비스 (채움)

채움 프로젝트입니다. 달력 기반의 식단(Meal) 관리 기능을 제공하며, 아래를 포함합니다.

- SPA를 위한 OAuth 2.1 Authorization Server(AS, PKCE)와 JWT 기반 Resource Server(RS)
- Calendar, MealCard, MealItem을 위한 REST API (아이템을 포함한 단일 호출 생성/수정 지원)
- MyBatis 기반 영속성 계층(운영: Oracle, 로컬/테스트: H2)
- 로컬(Vite 5173) 및 배포(Netlify) SPA에 대한 CORS 설정


## 기술 스택

- Java 17, Gradle
- Spring Boot 3.5.x (Web, Security, OAuth2 Authorization Server, Resource Server, Validation)
- MyBatis(XML 매퍼), Oracle JDBC, H2
- Lombok, Jackson


## 프로젝트 구조(일부)

- `src/main/java/com/bium/chaeum`
  - `security/SecurityConfig.java` – AS/RS 보안 체인, OAuth 클라이언트, CORS 설정
  - `application` – DTO 및 애플리케이션 서비스
  - `domain` – 엔티티, 리포지토리, 값 객체(VO)
  - `infrastructure/mybatis` – 매퍼 및 리포지토리 구현
- `src/main/resources/mybatis/mapper/*.xml` – MyBatis XML 매퍼


## 기술 스택
<img width="3004" height="1669" alt="Image" src="https://github.com/user-attachments/assets/cfdaf83e-a941-4332-9eb6-e7c32bfe18e1" />

## 사용자 시나리오
<img width="1373" height="1573" alt="Image" src="https://github.com/user-attachments/assets/30d69183-c311-42e0-a113-1c1dafb2d1c7" />

<img width="1383" height="1575" alt="Image" src="https://github.com/user-attachments/assets/cf1eb77a-7bbe-4790-a15d-d167024a0a36" />

## 기능 명세서
<img width="2662" height="1858" alt="Image" src="https://github.com/user-attachments/assets/86927fef-5e5a-43a2-86ed-26f74f871301" />

## ERD

<img width="3163" height="1629" alt="Image" src="https://github.com/user-attachments/assets/c5c1a307-0e82-45ec-8208-bef5ee918dc0" />

## 와이어 프레임

<img width="3639" height="911" alt="Image" src="https://github.com/user-attachments/assets/894061b6-257c-4c0e-89a3-642988e6e703" />

<img width="3639" height="710" alt="Image" src="https://github.com/user-attachments/assets/cef3574d-ecd1-400c-a4d4-f5aef9de8ce9" />

## 개발 멤버

| <img src="https://avatars.githubusercontent.com/u/62326659?v=4" width="130" height="130"> | <img src ="https://avatars.githubusercontent.com/u/219790960?v=4" width="130" height="130"> | <img src ="https://avatars.githubusercontent.com/u/181330888?v=4" width="130" height="130"> |
| :---------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: |
|                         [나규태](https://github.com/ncb6206)                         |                          [이상우](https://github.com/leesangwoo-dev)                          |                             [정의탁](https://github.com/uitak-dev)                             |
