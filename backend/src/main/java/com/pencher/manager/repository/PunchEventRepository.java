package com.pencher.manager.repository;

import com.pencher.manager.entity.PunchEvent;
import com.pencher.manager.entity.enums.PunchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PunchEventRepository extends JpaRepository<PunchEvent, Long> {
    List<PunchEvent> findByUserIdOrderByPunchAtDesc(Long userId, org.springframework.data.domain.Pageable pageable);
    List<PunchEvent> findByUserIdAndPunchAtBetweenOrderByPunchAtAsc(Long userId, Instant start, Instant end);

    @Query("SELECT p FROM PunchEvent p WHERE p.user.id = :userId AND p.punchAt >= :dayStart AND p.punchAt < :dayEnd ORDER BY p.punchAt ASC")
    List<PunchEvent> findByUserIdAndDate(@Param("userId") Long userId, @Param("dayStart") Instant dayStart, @Param("dayEnd") Instant dayEnd);

    @Query("SELECT p FROM PunchEvent p WHERE p.user.id = :userId AND p.punchType = :type AND p.punchAt >= :dayStart AND p.punchAt < :dayEnd")
    Optional<PunchEvent> findFirstByUserIdAndPunchTypeAndDate(
            @Param("userId") Long userId, @Param("type") PunchType type, @Param("dayStart") Instant dayStart, @Param("dayEnd") Instant dayEnd);

    @Query("SELECT p FROM PunchEvent p WHERE p.user.team.id = :teamId AND p.punchAt >= :dayStart AND p.punchAt < :dayEnd ORDER BY p.punchAt ASC")
    List<PunchEvent> findByTeamIdAndDate(@Param("teamId") Long teamId, @Param("dayStart") Instant dayStart, @Param("dayEnd") Instant dayEnd);

    @Query("SELECT p FROM PunchEvent p WHERE p.user.department.id = :departmentId AND p.punchAt >= :dayStart AND p.punchAt < :dayEnd ORDER BY p.punchAt ASC")
    List<PunchEvent> findByDepartmentIdAndDate(@Param("departmentId") Long departmentId, @Param("dayStart") Instant dayStart, @Param("dayEnd") Instant dayEnd);

    @Query("SELECT p FROM PunchEvent p WHERE p.user.id IN :userIds AND p.punchAt >= :from AND p.punchAt < :to ORDER BY p.punchAt ASC")
    List<PunchEvent> findByUserIdsAndDateRange(@Param("userIds") List<Long> userIds, @Param("from") Instant from, @Param("to") Instant to);
}
