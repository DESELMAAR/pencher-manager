package com.pencher.manager.repository;

import com.pencher.manager.entity.PlanningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanningRecordRepository extends JpaRepository<PlanningRecord, Long> {
    Optional<PlanningRecord> findByUserIdAndPlanDate(Long userId, LocalDate planDate);
    List<PlanningRecord> findByUserIdAndPlanDateBetweenOrderByPlanDateDesc(Long userId, LocalDate start, LocalDate end);
}
