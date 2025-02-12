package com.wbm.scenergyspring.domain.portfolio.repository;

import com.wbm.scenergyspring.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("""
            select p from Portfolio p
            where p.userId = :userId
            """)
    Optional<Portfolio> findByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);
}
