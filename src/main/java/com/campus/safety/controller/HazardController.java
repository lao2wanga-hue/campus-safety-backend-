package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.HazardDTO;
import com.campus.safety.dto.HazardUpdateDTO;
import com.campus.safety.entity.Hazard;
import com.campus.safety.service.HazardService;
import com.campus.safety.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hazard")
@RequiredArgsConstructor
public class HazardController {
    
    private final HazardService hazardService;
    private final JwtUtil jwtUtil;
    
    @PostMapping
    public Result<Void> create(@RequestBody @Validated HazardDTO dto) {
        hazardService.create(dto);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HazardUpdateDTO dto) {
        hazardService.update(id, dto);
        return Result.success();
    }
    
    @PostMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @RequestParam Long handlerId) {
        hazardService.assign(id, handlerId);
        return Result.success();
    }
    
    @PostMapping("/{id}/resolve")
    public Result<Void> resolve(@PathVariable Long id, @RequestParam String resolution) {
        hazardService.resolve(id, resolution);
        return Result.success();
    }
    
    @PostMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        hazardService.close(id);
        return Result.success();
    }
    
    @GetMapping("/{id}")
    public Result<Hazard> getById(@PathVariable Long id) {
        return Result.success(hazardService.getById(id));
    }
    
    @GetMapping("/list")
    public Result<List<Hazard>> getList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(hazardService.getList(status, level, page, size));
    }
    
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(hazardService.getStatistics());
    }
    
    @GetMapping("/my-reports")
    public Result<List<Hazard>> getMyReports() {
        Long userId = jwtUtil.getCurrentUserId();
        return Result.success(hazardService.getMyReports(userId));
    }
    
    @GetMapping("/my-tasks")
    public Result<List<Hazard>> getMyTasks() {
        Long userId = jwtUtil.getCurrentUserId();
        return Result.success(hazardService.getMyTasks(userId));
    }
}