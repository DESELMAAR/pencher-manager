package com.pencher.manager.service;

import com.pencher.manager.dto.TeamRequest;
import com.pencher.manager.dto.TeamResponse;
import com.pencher.manager.entity.Team;

import java.util.List;

public interface TeamService {
    TeamResponse create(TeamRequest request);
    List<TeamResponse> findAll();
    TeamResponse findById(Long id);
    TeamResponse update(Long id, TeamRequest request);
    void delete(Long id);
    List<TeamResponse> findByDepartmentId(Long departmentId);
    Team getTeamEntity(Long id);
}
