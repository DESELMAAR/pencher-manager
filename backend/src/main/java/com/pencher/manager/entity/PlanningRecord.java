package com.pencher.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "planning_records", indexes = {
    @Index(name = "idx_planning_user_date", columnList = "user_id, plan_date", unique = true),
    @Index(name = "idx_planning_date", columnList = "plan_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "planned_start_time")
    private LocalTime plannedStartTime;

    @Column(name = "planned_end_time")
    private LocalTime plannedEndTime;

    @Column(name = "scheduled")
    private Boolean scheduled;

    @Column(name = "shift_type", length = 50)
    private String shiftType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
