package com.example.domain.fundRecommend.repository;

import com.example.domain.fundRecommend.entity.FundRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundRecommendRepository extends JpaRepository<FundRecommend, Long> {
}
