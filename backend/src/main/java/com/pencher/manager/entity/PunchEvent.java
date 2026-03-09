package com.pencher.manager.entity;

import com.pencher.manager.entity.enums.PunchType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "punch_events", indexes = {
    @Index(name = "idx_punch_events_user_id", columnList = "user_id"),
    @Index(name = "idx_punch_events_punch_at", columnList = "punch_at"),
    @Index(name = "idx_punch_events_user_date", columnList = "user_id, punch_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PunchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "punch_type", nullable = false)
    private PunchType punchType;

    @Column(name = "punch_at", nullable = false)
    private Instant punchAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
