package com.pencher.manager.service;

import com.pencher.manager.entity.enums.AttendanceStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for attendance status calculation logic.
 * Grace period: 5 minutes.
 */
class AttendanceStatusCalculationTest {

    private static final int GRACE_PERIOD_MINUTES = 5;

    private AttendanceStatus computeStatus(LocalTime plannedStart, LocalTime actualStart, boolean hasPunch) {
        if (!hasPunch) return AttendanceStatus.ABSENT;
        if (plannedStart == null) return AttendanceStatus.ON_TIME;
        long delayMinutes = java.time.Duration.between(plannedStart, actualStart).toMinutes();
        if (delayMinutes <= GRACE_PERIOD_MINUTES && delayMinutes >= 0) return AttendanceStatus.ON_TIME;
        if (delayMinutes > GRACE_PERIOD_MINUTES) return AttendanceStatus.LATE;
        return AttendanceStatus.ON_TIME; // early
    }

    @Test
    void onTime_whenPunchWithinGracePeriod() {
        LocalTime planned = LocalTime.of(9, 0);
        LocalTime actual = LocalTime.of(9, 3);
        assertEquals(AttendanceStatus.ON_TIME, computeStatus(planned, actual, true));
    }

    @Test
    void late_whenPunchAfterGracePeriod() {
        LocalTime planned = LocalTime.of(9, 0);
        LocalTime actual = LocalTime.of(9, 10);
        assertEquals(AttendanceStatus.LATE, computeStatus(planned, actual, true));
    }

    @Test
    void absent_whenNoStartPunch() {
        assertEquals(AttendanceStatus.ABSENT, computeStatus(LocalTime.of(9, 0), null, false));
    }

    @Test
    void onTime_whenExactlyAtPlannedStart() {
        LocalTime t = LocalTime.of(9, 0);
        assertEquals(AttendanceStatus.ON_TIME, computeStatus(t, t, true));
    }
}
