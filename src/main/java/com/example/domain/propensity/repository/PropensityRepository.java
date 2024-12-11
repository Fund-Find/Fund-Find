package com.example.domain.propensity.repository;

import com.example.domain.propensity.entity.Propensity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropensityRepository extends JpaRepository<Propensity, Long> {
    Optional<Propensity> findById(Long id);
}

