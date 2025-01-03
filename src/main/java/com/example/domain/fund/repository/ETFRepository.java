package com.example.domain.fund.repository;

import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ETFRepository extends JpaRepository<ETF, String> {
    List<ETF> findByCategory(ETFCategory category);
    List<ETF> findBySubCategory(ETFSubCategory subCategory);
    List<ETF> findByCategoryAndSubCategory(ETFCategory category, ETFSubCategory subCategory);
    Optional<ETF> findByCode(String code); // 즐겨찾기 기능을 위한 코드
}
