# 금융 교육 & 사용자 맞춤 ETF 추천 플랫폼

<!--배지-->
![Repository Size][repository-size-shield] ![Issue Closed][issue-closed-shield]

<!-- 자바 -->
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">
<!-- 스프링부트 -->
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<!-- 스프링 시큐리티 -->
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
<!-- docker -->
<img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> 
<!-- HTML5 -->
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white">
<!-- CSS3 -->
<img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white">
<!-- JavaScript -->
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
<!-- React -->
<img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white">
<!-- Git -->
<img src="https://img.shields.io/badge/Git-2088FF?style=for-the-badge&logo=Git&logoColor=white">
<!-- Github -->
<img src="https://img.shields.io/badge/GitHub-2088FF?style=for-the-badge&logo=GitHub&logoColor=white">
<!-- Swiper Library -->
<img src="https://img.shields.io/badge/Swiper%20EC2-FF9900?style=for-the-badge&logo=Swiper%20EC2&logoColor=white">
<!-- Fetch API -->
<img src="https://img.shields.io/badge/Fetch API-%23F46800.svg?style=for-the-badge&logo=Fetch API&logoColor=white">



<!--목차-->
# 목차
- [[1] 프로젝트 설명](#1-프로젝트-설명)
  - [프로젝트 설명](#프로젝트-설명)
  - [개발 기간](#개발-기간)
  - [개발 환경](#개발-환경)
  - [기술 스택](#기술-스택)
- [[2] 팀원 역할분담](#2-팀원-역할분담)
  - [개발 팀원 및 역할 분담](#개발-팀원-및-역할-분담)
- [[3] 문서](#3-문서)
  - [ERD](#ERD)
  - [요구사항 정의서](#요구사항-정의서)
  - [요구사항 기획서](#요구사항-기획서)
  - [와이어프레임](#와이어프레임)
  - [DFD](#DFD)
- [[4] 주요 기능](#4-주요-기능)
  - [회원](#회원)
  - [금융 퀴즈](#금융-퀴즈)
  - [ETF](#ETF)
- [[5] 페이지별 기능](#5-페이지별-기능)
  - [메인 페이지](#메인-페이지)
  - [회원가입 페이지](#회원가입-페이지)
  - [로그인 페이지](#로그인-페이지)
  - [아이디 찾기](#아이디-찾기)
  - [비밀번호 재발급](#비밀번호-재발급)
  - [프로필 페이지](#프로필-페이지)
  - [ETF 목록 페이지](#ETF-목록-페이지)
  - [ETF 투자성향 분석 결과 팝업](#ETF-투자성향-분석-결과-팝업)
  - [ETF 상세정보 페이지](#ETF-상세정보-페이지)
  - [퀴즈쇼 페이지](#퀴즈쇼-페이지)
  - [금융 퀴즈 문제 풀기 페이지](#금융-퀴즈-문제-풀기-페이지)
- [[6] 트러블 슈팅](#6-트러블-슈팅)
  - [트러블 슈팅 1](#트러블-슈팅-1) #15 유저 로그인 , 회원가입 페이지가 프론트와 백연결과정에 문제가 생김
  - [트러블 슈팅 2](#트러블-슈팅-2) #1 Kodex ETF 200 종목 출력하는데 어려움
  - [트러블 슈팅 3](#트러블-슈팅-3) #6 Kodex 200 ETF 종목명 출력 문제, Kodex 200 ETF 구성종목명 출력 문제
  - [트러블 슈팅 4](#트러블-슈팅-4) #40 ETF 상세정보 페이지 출력 시 css파일이 제대로 작동하지 않음
  - [트러블 슈팅 5](#트러블-슈팅-5) #68 순환 참조 에러로, 퀴즈 생성이 안되는 문제
- [[7] 개선 목표](#7-개선-목표)
  - [개선 목표](#개선-목표)
- [[8] 프로젝트 후기](#8-프로젝트-후기)
  - [임재혁](#임재혁)
  - [장준규](#장준규)
  - [한태호](#한태호)

# [1] 프로젝트 설명

* ❗️제작 동기❗️
  - 가계부채 증가, 저금리 기조, 부동산 및 주식 등 자산 시장 변동성이 커지면서, 개인이 금융지식 없이 자산을 운용하기 점점 어렵고 위험해지고 있음
  - 금융 교육을 통해 합리적 소비와 투자, 리스크 관리, 올바른 재무 목표 설정 등을 익힘으로써 개인의 재정 건전성을 높이고 사회 전반의 금융 안정성을 도모할 수 있음
  - 최근 주식, ETF 시장에 대한 관심이 높아지고 있지만, 정작 개인 투자자들 중에는 자신만의 투자 성향을 명확히 인지하지 못한 채 트렌드만 쫒아 매매하는 경우가 많음
  - 이는 불필요한 손실을 유발하거나, 장기적인 투자 목표 설정에 어려움을 겪게 만드는 원인이 됨
  - 사용자의 투자 성향을 분석하여 맞춤형 ETF를 추천해주어 이러한 문제를 해결하고 더 나아가 지속 가능한 투자 활동을 돕는 것이 본 프로젝트의 제작동기임

## 프로젝트 설명
  - 기본적으로 제공하는 퀴즈를 기반으로 지식을 쌓을 수 있음
  - 사용자가 직접 원하는 대로 퀴즈를 생성하고 수정할 수 있음 
  - 투자성향분석 설문을 바탕으로 자신의 투자성향을 파악하고 맞춤형 ETF를 추천해줌

## 개발 기간
  - 2024.11.11 ~ 2024.12.20

## 개발 환경

* 환경 & IDE
  - 운영체제 : window 11
  - Intellij
  - Visual Studio Code
  - Dbeaver
  - Docker Desktop
  - Postman

* Version
  - openjdk version: 17.0.13 
  - Gradle JVM: corretto-17(Amazon Corretto 17.0.13)
  - SpringBoot version: 3.3.5
  - spring.dependency-management plugin version: 1.1.6

* DB
  - postgreSQL version -> 17.0
  - DB PORT: 5433
  - DB username: ff_dev
  - 데이터베이스 이름 : ff_dev

## 기술 스택

* Version Control
  - Github
  - Git
    
* Backend Technologies
  - Java
  - SpringBoot
    
* FrontEnd Technologies
  - HTML5
  - CSS3
  - React
  - Fetch API
  - Swiper Library

* DB
  - PostgreSQL
 
<div align="right">
  
[목차로 이동](#목차)

</div>

# [2] 팀원 역할분담


## 개발 팀원 및 역할 분담

- 임재혁(팀원)

  * UI

  1) 퀴즈
     
     - 퀴즈 조회
     - 인기 퀴즈
     - 사용자별 퀴즈 등록
     - 퀴즈 수정
     - 퀴즈 삭제
  
- 장준규(팀원)

  * UI
    
    - 회원가입 페이지
    - 로그인 페이지
    - 아이디, 비밀번호 찾기 페이지
    - 프로필 페이지
    - Nav 바

  1) 회원

     - 회원가입
     - 일반 로그인
     - 아이디 찾기
     - 비밀번호 찾기
     - 로그아웃
     - 로그인 갱신(30분)

  2) 프로필

     - 투자성향 MBTI 조회
     - 썸네일 이미지 등록/조회/수정
     - 자기소개 조회/수정
     - 닉네임 조회/수정
     
- 한태호(팀장)

  * UI

    - 메인 페이지
    - 전체 ETF 조회 페이지
    - ETF 상세 페이지
    - ETF 상세 비교 팝업 창
    - 투자성향분석 MBTI 팝업 창
    - 투자성향분석 결과 페이지

  1) ETF
     
     - ETF 조회, 검색
     - ETF 상세 비교
     - ETF 즐겨찾기 등록, 삭제
     - 투자성향 MBTI 설문 결과에 따른 맞춤 ETF 추천(NAV, ETF 현재가격 활용)
     - ETF 등락률 순위 조회
     - ETF 기본 정보 및 구성종목 상세 조회

  2) 투자성향 분석
 
     - 투자성향 MBTI 분석
     - 투자성향 MBTI 1일 2회 제한

<div align="right">
  
[목차로 이동](#목차)

</div>

# [3] 문서

## ERD
- https://www.erdcloud.com/d/jivZFQrf2wXvugu2Q

## 요구사항 정의서
- https://docs.google.com/spreadsheets/d/13flPhRkAohFWRyeVgpO6fK2K0rhyp3mD/edit?usp=sharing&ouid=114994970396661234746&rtpof=true&sd=true

## 요구사항 기획서
- https://docs.google.com/document/d/1uyWmLjlEamgZz6NCSunFm4GFJRT1n7Vc/edit?usp=sharing&ouid=114994970396661234746&rtpof=true&sd=true

## 와이어프레임
- https://www.figma.com/design/IYYwOXqcTOrugz7xzfTeiS/Fund-Find?node-id=0-1&t=IdCUjNJuED7oRxFM-1

## DFD
- https://www.figma.com/board/eFCqOQvEn6oGs17G7hX7LP/Fund-Find?node-id=0-1&t=ISUhGe4DFFLt1AXA-1

<div align="right">
  
[목차로 이동](#목차)

</div>

# [4] 주요 기능

## 회원
>
- 회원가입
- 일반 로그인
- 아이디 찾기
- 비밀번호 재발급
- 프로필 조회 및 수정

## 금융 퀴즈
>
- 인기 금융 퀴즈 조회
- 금융 퀴즈 제작
- 금융 퀴즈 수정
- 금융 퀴즈 삭제

## ETF 
>
- 투자 성향 분석(설문 조사)
- 사용자 맞춤 ETF 제공
- ETF 비교 기능 제공
- ETF 즐겨찾기 기능
- ETF 조회
- ETF 상세 페이지

<div align="right">
  
[목차로 이동](#목차)

</div>

# [5] 페이지별 기능

## 메인 페이지
![image](https://github.com/user-attachments/assets/d6b1fc53-283c-4b36-809f-648831139a5c)
- 투자성향 MBTI 분석
- 로그인 기능
- 인기퀴즈 조회
- 등락률 Best Fund 조회
- 로그인 시간 갱신

## 회원가입 페이지
![image](https://github.com/user-attachments/assets/f36aba67-207a-4f31-a575-b864a5f208ba)
- 회원가입 기능

## 로그인 페이지
![image](https://github.com/user-attachments/assets/f2d69fa0-191f-4d60-9111-46cc17727661)
- 로그인 기능

## 아이디 찾기 
![image](https://github.com/user-attachments/assets/d5b6aadd-42e5-43be-90f0-2e2a7fcd0a52)
- 아이디 찾기 기능

## 비밀번호 재발급 
![image](https://github.com/user-attachments/assets/392686b8-fef9-4ef5-9b62-d1f29cffd18d)
- 비밀번호 재발급 기능
  
## 프로필 페이지
![image](https://github.com/user-attachments/assets/2c95eede-0030-45c6-a7b7-84187890dd12)
- 프로필 이미지 등록 및 변경(파일 업로드)
- 닉네임 변경
- 자기소개 변경
- 사용자ID, 이메일, 투자성향 MBTI 조회
- 비밀번호 변경 기능

## ETF 목록 페이지
![image](https://github.com/user-attachments/assets/fa034d3f-9152-4c9b-90e4-ee9a488b5f12)
- 투자성향 MBTI 분석
  ![image](https://github.com/user-attachments/assets/5eacb308-28af-4e5e-af5d-5a9b90d965b2)
- ETF 검색 기능
  ![image](https://github.com/user-attachments/assets/1d9d0342-be0a-4392-bcf7-64c13f897b73)
- ETF 비교 기능
  ![image](https://github.com/user-attachments/assets/559207dc-cbfe-4b33-83a6-0d9321613213)
- ETF 즐겨찾기 기능
  ![image](https://github.com/user-attachments/assets/f7a2bf29-da6b-4565-a61e-2c42b9e02152)
- 등락률 Best Fund 조회 기능

## ETF 투자성향 분석 결과 팝업
![image](https://github.com/user-attachments/assets/06da57a0-c9dc-47c1-a1d0-be1348146e97)
- ETF 투자성향 분석 결과 조회

## ETF 상세정보 페이지
![image](https://github.com/user-attachments/assets/e2ceb1be-1d1a-42e7-a68e-278789d1af7f)
- ETF 상세 정보 조회
- ETF 구성종목 정보 조회

## 퀴즈쇼 페이지
![image](https://github.com/user-attachments/assets/38d71cc5-8dce-4e34-967f-b8309cd98150)
- 등록된 퀴즈 조회
- 퀴즈쇼 생성 기능

## 금융 퀴즈 문제 풀기 페이지
![image](https://github.com/user-attachments/assets/bf169d8c-872f-4d19-807a-20fb330e2dc0)
- 객관식, OX, 단답형 문제 풀기
- 정답, 오답 문제 확인 및 점수 산출 기능

<div align="right">
  
[목차로 이동](#목차)

</div>

# [6] 트러블 슈팅

## 트러블 슈팅 1

### 🚨 #15
### 🚧 유저 로그인 , 회원가입 페이지가 프론트와 백연결과정에  문제가 생김

A. 이슈 내역
둘이 다루는 서버가 다르기 때문에 cros를  해줘야함 .
이떄 기존에 배웠던 방식으로 문제해결을 시도하였으나 해결되지 않음 .

<br>
-
<br>

## 😱 문제점 설명
```
package com.example.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://cdpn.io", "http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

해당 코드를 WebMvcConfig 파일을 만들어서 작성하여 local이 다른 서버들끼리 연결할수 있도록 배웠지만 해결되지 않음.

<br> 
-
<br>

## 🛑 원인
이는 이전 버전의 스프링 프레임워크에서의 방법으로 지금버전에선 보안이 강화되어 추가로  
```
@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // 허용할 출처 추가
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 요청 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 및 인증 정보 포함 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 CORS 정책 적용
        return source;
    }
```
    처럼 추가적인 cros 설정이 필요하고 security 설정 에 추가로 
    `.cors(cors -> cors.configurationSource(corsConfigurationSource())) `로 crsf 설정도 축로 해줘야한다 .

<br> 
-
<br>

## 🚥 해결
해당 코드를 추가하여 react와 인텔리제이 서버의 연결을 할 수 있게 되었다.
<br>
- 
<br>

## 트러블 슈팅 2

### 🚨 #1 
### 🚧 Kodex ETF 200 종목 출력하는데 어려움

A. 이슈 내역
<br>
Bearer 토큰을 발급 받아 Kodex 200 ETF 정보를 화면에 출력
<br>

## 😱 문제점 설명
<br> 
@GetMapping을 활용해 stock.html 파일에 랜더링 하는 과정에서 404 에러 발생하였습니다.
<br>
이는 경로나 오타 문제가 아니었습니다.
<br>

## 🛑 원인

<br> 
- 한국투자증권 open API 문서에 pom.xml 파일을 활용해야 한다는 이야기에 pom.xml 파일을 새로 만든 것이 원인이었음
<br>
- pom.xml의 파일 내용은 maven 형식으로 build.gradle과 충돌하여 발생한 문제였음
<br>

## 🚥 해결

<br>
- pom.xml 파일 형식을 gradle 형식으로 변경하여 build.gradle에 넣어 문제 해결
<br>


## 😱 문제점 설명

postman에서 Bearer 토큰 발급에 문제 발생

## 🛑 원인

<br>
- 한국투자증권에서 App Key와 App Secret Key를 발급 받아 사용하였는데, 형식에 맞지 않는 개행 문자가 삽입되어 발생
<br>

## 🚥 해결
- 개행 문자를 삭제하여 해결하였음

## 트러블 슈팅 3

### 🚨 #6  
### 🚧 이슈 제목

A. 이슈 내역

<br>
- Kodex 200 ETF 종목명 출력 문제
<br>
- Kodex 200 ETF 구성종목 출력 문제
<br>

## 😱 문제점 설명

<br> 
- Kodex 200 ETF 종목 명이 출력되지 않았음
<br>

## 🛑 원인

<br> 
- 한국투자증권 Open Api 문서에 설명된 것과 다른 엔드포인트와 tr_id 사용
<br>

## 🚥 해결

<br>
- 엔드포인트 : /uapi/etfetn/v1/quotations/inquire-price 사용
- tr_id : FHPST02400000 사용
<br>


## 😱 문제점 설명

<br>
- Kodex 200 ETF 구성종목 출력 시 Postman에는 정상 출력 되지만, 로그를 찍어보면 output2 데이터가 출력되지 않았음 
<br>

## 🛑 원인

<br>
- output2는 output1과 다르게 배열 형태로 되어 있었던 것이 원인
<br>

## 🚥 해결
- output2 데이터가 배열로 존재하는지 여부를 확인 후에 반복문을 통해 처리

## 트러블 슈팅 4

### 🚨 #40 
### 🚧 

A. 이슈 내역
<br>
- ETF 상세정보 페이지 출력 시 css파일이 제대로 작동하지 않음
<br>

## 😱 문제점 설명
<br> 
-   ETF 상세정보 페이지(ETFDetail.jsx)를 출력 시 가독성이 떨어져 css 작업 수정 중 css가 제대로 작동하지 않은 문제 발생
<br>

## 🛑 원인

<br> 
- ETFList.jsx에 영향을 받아 수정한 css 코드가 작동하지 않는 문제
<br>

## 🚥 해결

<br>
- ETFDetail..jsx는 컴포넌트 단위로 여러 페이지에 사용하여 분리
<br>
<br>
- 기존의 etfDetail.css 파일을 module 패키지를 만들어 ETFDetail.module.css 파일로 변경하고, 클래스명을 카멜케이스 형식으로 변경
<br> 
<br>
- ETFDetail.jsx파일에 ETFDetail.module.css를 import한 후 HTML의 모든 클래스 명을 {styles.클래스명} 형식으로 변경하여 문제 해결
<br>

## 트러블 슈팅 5

### 🚨 #68
### 🚧 퀴즈쇼 생성

A. 이슈 내역

<br>
- 퀴즈 생성이 안되는 문제
<br>

## 😱 문제점 설명

<br> 
- 순환 참조 문제로 퀴즈 생성이 안됨 
<br>

## 🛑 원인

<br> 
순환 참조 흐름

QuizShowCreateRequest (DTO)
안에 List<QuizRequest> quizzes가 있음.
그런데 여기 **QuizRequest**에 List<QuizChoice> choices가 그대로 들어옴.
즉, 엔티티(QuizChoice)를 그대로 DTO에 넣고 있음.

QuizChoice (엔티티)
이 엔티티는 @ManyToOne Quiz quiz 로 **Quiz**를 참조.
Quiz (엔티티)

이 엔티티는 @OneToMany List<QuizChoice> choices로 **QuizChoice**들을 다시 참조 → (양방향)
또한 @ManyToOne QuizShow quizShow로 **QuizShow**를 참조.

QuizShow (엔티티)
이 엔티티는 @OneToMany List<Quiz> quizzes로 **Quiz**들을 참조(양방향).
또한 @ManyToMany Set<SiteUser> votes 에서 **SiteUser**와의 관계가 있고, @JsonManagedReference 사용 중.

결국, Jackson이 objectMapper.readValue(...) 로 JSON → QuizShowCreateRequest를 역직렬화할 때,
QuizRequest.choices 가 실제 엔티티(QuizChoice)이고, 내부에 quiz → quizShow → votes → SiteUser 등 계속 참조가 연결되면서 Jackson이 “이건 @JsonBackReference / @JsonManagedReference로 이미 관리되는 객체인데, 타입이 맞지 않는다”는 식으로 에러 발생.

요컨대, DTO에 엔티티(특히 양방향 관계가 있는 엔티티) 를 넣어두면 Jackson이 순환 참조를 감지하고 예외가 발생하기 쉬움

## 🚥 해결
 
1. 엔티티와 DTO 완전 분리
- 모든 Request/Response에 대한 전용 DTO 사용
- 엔티티 참조 제거로 순환 참조 방지

2. 계층별 책임 명확화
- Controller: 요청 검증 및 응답 처리
- Service: 비즈니스 로직 및 엔티티 변환
- Repository: 데이터 접근 

3. 검증 로직 강화
- Jakarta Validation 사용
- DTO 레벨에서의 데이터 유효성 검증

4. 안전한 객체 변환
- Builder 패턴 활용
- 명시적인 매핑 메서드 구현

<div align="right">
  
[목차로 이동](#목차)

</div>

# [7] 개선 목표
## 개선 목표
  - 메인 페이지 디자인 및 CSS 수정
  - 전체 ETF 로딩시간 단축
  - 코드 가독성 향상
 
<div align="right">
  
[목차로 이동](#목차)

</div>

# [8] 프로젝트 후기

## 임재혁

## 장준규

## 한태호
프로젝트를 마무리하며, 이 특별한 여정을 함께한 팀원들에게 깊은 감사의 마음을 전합니다.

돌이켜보면, 가장 값진 순간들은 우리가 기술적 난관 앞에서 포기하지 않고 함께 성장해 나갔던 시간들입니다. JWT 인증으로 고민하던 순간, RESTful API 설계에 막혔을 때, 처음 사용해보는 React 문제로 머리를 맞대었던 순간... 매번 에러가 발생할 때마다 함께 모니터 앞에 모여 열띤 토론을 이어갔습니다. "이 에러는 도대체 왜 나는 거야?"라며 한숨짓다가도, 함께 웃으며 해결책을 찾아가는 과정 자체가 큰 배움이었습니다.

Fund-Find는 저에게 단순한 프로젝트 그 이상의 의미를 가집니다. 팀장으로서 기술적 성장은 물론, 동료들과 함께 문제를 해결해나가는 진정한 리더십이 무엇인지 깨달을 수 있었습니다. 때로는 제가 부족한 부분을 팀원들이 채워주었고, 때로는 제가 팀원들의 어려움을 해결하는데 도움을 줄 수 있었던 이 모든 순간들이 우리를 한 단계 더 성장시켰다고 믿습니다.

이제는 이 소중한 경험을 발판으로, 사용자들의 실제 고민을 해결하고 그들의 삶에 의미 있는 변화를 만들어내는 개발자로 성장하고 싶습니다. 다시 한 번, 이 값진 여정에 함께해준 모든 팀원들에게 진심 어린 감사를 전합니다.

<div align="right">
  
[목차로 이동](#목차)

</div>
