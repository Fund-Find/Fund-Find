package com.example.domain.favorite.service;

import com.example.domain.favorite.entity.ETFFavorite;
import com.example.domain.favorite.repository.ETFFavoriteRepository;
import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.repository.ETFRepository;
import com.example.domain.user.entity.SiteUser;
import com.example.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ETFFavoriteService {
    private final ETFFavoriteRepository favoriteRepository;
    private final ETFRepository etfRepository;
    private final UserService userService;

    public ETFFavorite addFavorite(String etfCode, SiteUser user) {
        ETF etf = etfRepository.findByCode(etfCode).get();
        return favoriteRepository.findByUserAndEtf(user, etf).orElseGet(() -> {
            ETFFavorite favorite = ETFFavorite.builder()
                    .user(user)
                    .etf(etf)
                    .build();
            return favoriteRepository.save(favorite);
        });
    }

    public void removeFavorite(String etfCode, SiteUser user) {
        ETF etf = etfRepository.findByCode(etfCode).get();
        favoriteRepository.findByUserAndEtf(user, etf).ifPresent(favoriteRepository::delete);
    }

    public List<ETFFavorite> getFavorites(SiteUser user) {
        System.out.println("Fetching favorites for user: " + user.getId());
        List<ETFFavorite> favorites = favoriteRepository.findByUser(user);
        System.out.println("Favorites retrieved: " + favorites.size());
        return favorites;
    }

}
