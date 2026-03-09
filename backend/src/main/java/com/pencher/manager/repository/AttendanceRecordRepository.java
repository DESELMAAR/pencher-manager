package com.pencher.manager.repository;

import com.pencher.manager.entity.AttendanceRecord;
import com.pencher.manager.entity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate date);
    List<AttendanceRecord> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long userId, LocalDate start, LocalDate end, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.user.team.id = :teamId AND a.attendanceDate = :day")
    List<AttendanceRecord> findByTeamIdAndDate(@Param("teamId") Long teamId, @Param("day") LocalDate date);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.user.department.id = :departmentId AND a.attendanceDate = :day")
    List<AttendanceRecord> findByDepartmentIdAndDate(@Param("departmentId") Long departmentId, @Param("day") LocalDate date);

    List<AttendanceRecord> findByAttendanceDateAndAttendanceStatus(LocalDate date, AttendanceStatus status);
    List<AttendanceRecord> findByAttendanceDate(LocalDate date);
    long countByAttendanceDateAndAttendanceStatus(LocalDate date, AttendanceStatus status);
}
