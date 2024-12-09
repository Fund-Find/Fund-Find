package com.example.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizValidationException extends RuntimeException{
    private final String message;
    private final List<String> errors;
}
