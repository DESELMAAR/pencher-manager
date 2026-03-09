package com.pencher.manager.dto;

import com.pencher.manager.entity.enums.PunchType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PunchEventResponse {
    private Long id;
    private Long userId;
    private PunchType punchType;
    private Instant punchAt;
    private Instant createdAt;
}
