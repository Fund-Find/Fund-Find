package com.example.domain.fund.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ViewController
    // 단순히 URL 요청을 특정 뷰에 매핑할 때 사용
    // 별도의 컨트롤러 클래스 없이 URL과 뷰의 연결만 처리
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // registry.addViewController
        // -> 루트 경로 /로 들어오는 요청을 처리함
        // -> http://localhost8080/ 요청이 이 설정에 매핑됨
        // .setViewName("home")
        // -> 요청된 URL에 대해 뷰 이름을 "home"으로 설정
        // -> Spring MVC는 뷰 이름을 기반으로 실제 뷰 파일을 찾음
        registry.addViewController("/").setViewName("home");
    }

    // ViewResolver
    // -> 컨트롤러에서 반환하는 뷰 이름을 실제 뷰 파일 경로로 변환하는 역할
    // -> Spring은 여러 뷰 리졸버를 지원하며, 여기에서는 JSP 기반 뷰 리졸버를 설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // registry.jsp("/templates/", ".html")
        // -> JSP 파일의 경로와 확장자를 설정함
        // -> /templates/ 디렉토리 아래에서 JSP 파일을 찾고, 파일 확장자는 .html로 지정함
        registry.jsp("/templates/", ".html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")  // 프론트엔드 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}


