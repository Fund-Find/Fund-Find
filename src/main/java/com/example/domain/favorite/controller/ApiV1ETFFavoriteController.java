package com.example.domain.favorite.controller;

import com.example.domain.favorite.service.ETFFavoriteService;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/etf/favorites")
public class ApiV1ETFFavoriteController {
    private final ETFFavoriteService favoriteService;
    private final UserService userService;

    @PostMapping("/add/{etfCode}")
    public ResponseEntity<RsData<String>> addFavorite(@PathVariable("etfCode") String etfCode, @CookieValue(value = "accessToken", required = false) String accessToken) {
        SiteUser user = this.userService.getSiteUserFromAccessToken(accessToken);
        favoriteService.addFavorite(etfCode, user);
        return ResponseEntity.ok(RsData.of("200", "즐겨찾기에 추가되었습니다.", null));
    }

    @DeleteMapping("/remove/{etfCode}")
    public ResponseEntity<RsData<String>> removeFavorite(@PathVariable("etfCode") String etfCode, @CookieValue(value = "accessToken", required = false) String accessToken) {
        SiteUser user = this.userService.getSiteUserFromAccessToken(accessToken);
        favoriteService.removeFavorite(etfCode, user);
        return ResponseEntity.ok(RsData.of("200", "즐겨찾기에서 제거되었습니다.", null));
    }

    @GetMapping("")
    public ResponseEntity<RsData<List<String>>> getFavorites(@CookieValue(value = "accessToken", required = false) String accessToken) {
        SiteUser user = this.userService.getSiteUserFromAccessToken(accessToken);
        List<String> favoriteEtfCodes = favoriteService.getFavorites(user)
                .stream()
                .map(favorite -> favorite.getEtf().getCode())
                .collect(Collectors.toList());
        return ResponseEntity.ok(RsData.of("200", "즐겨찾기 목록 조회 성공", favoriteEtfCodes));
    }
}
