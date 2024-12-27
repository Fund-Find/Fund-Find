package com.example.domain.favorite.repository;

import com.example.domain.favorite.entity.ETFFavorite;
import com.example.domain.fund.entity.ETF;
import com.example.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ETFFavoriteRepository extends JpaRepository<ETFFavorite, Long> {
    List<ETFFavorite> findByUser(SiteUser user);
    Optional<ETFFavorite> findByUserAndEtf(SiteUser user, ETF etf);
}
