package com.example.domain.propensity.controller;

import com.example.domain.propersity.dto.PropensityDTO;
import com.example.domain.propersity.repository.PropensityRepository;
import com.example.domain.propersity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/propensity")
public class ApiV1PropensityController {

    private final PropensityRepository propensityRepository;
    private final PropensityService propensityService;

    @PostMapping("/survey")
    public ResponseEntity<?> savePropensity(@RequestBody Map<String, String> answers) {
        try {
            PropensityDTO savedPropensity = propensityService.processAndSavePropensity(answers);
            return ResponseEntity.ok(savedPropensity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save propensity: " + e.getMessage());
        }
    }

}
