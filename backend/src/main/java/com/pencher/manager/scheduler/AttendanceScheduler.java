package com.pencher.manager.scheduler;

import com.pencher.manager.repository.UserRepository;
import com.pencher.manager.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Daily job: compute attendance for all users who punched START_WORK today.
 * Runs at 23:00 so end-of-day state is finalized.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final UserRepository userRepository;
    private final AttendanceService attendanceService;

    @Scheduled(cron = "${pencher.scheduler.attendance-cron:0 0 23 * * ?}")
    public void computeDailyAttendance() {
        LocalDate today = LocalDate.now();
        List<Long> userIds = userRepository.findAll().stream()
                .map(u -> u.getId())
                .toList();
        for (Long userId : userIds) {
            try {
                attendanceService.computeAndSaveAttendance(userId, today);
            } catch (Exception e) {
                log.warn("Failed to compute attendance for user {}: {}", userId, e.getMessage());
            }
        }
        log.info("Attendance computation finished for date {}", today);
    }
}
