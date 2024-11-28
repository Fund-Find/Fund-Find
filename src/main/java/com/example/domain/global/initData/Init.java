package com.example.domain.global.initData;

import com.example.domain.quizShow.service.QuizShowService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Init {
    @Bean
    CommandLineRunner initData(QuizShowService quizShowService) {
        return args -> {
            quizShowService.write("제목1", "설명1", 5, 10, 0);
            quizShowService.write("제목2", "설명2", 10, 30, 0);
            quizShowService.write("제목3", "설명3", 15, 55, 0);
            quizShowService.write("제목4", "설명4", 5, 20, 0);
            quizShowService.write("제목5", "설명5", 15, 15, 0);
        };
    }
}
