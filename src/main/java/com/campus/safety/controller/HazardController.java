package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.*;
import com.campus.safety.entity.Hazard;
import com.campus.safety.service.HazardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "隐患管理", description = "隐患上报、分配、整改、统计")
@RestController
@RequestMapping("/api/hazard")
public class HazardController {

    @Autowired
    private HazardService hazardService;

    @Operation(summary = "隐患列表")
    @GetMapping("/list")
    public Result<List<Hazard>> list(@RequestParam(required = false) String status) {
        List<Hazard> list = hazardService.listByStatus(status);
        return Result.success(list);
    }

    @Operation(summary = "隐患详情")
    @GetMapping("/detail/{id}")
    public Result<HazardDetailDTO> detail(@PathVariable Long id) {
        HazardDetailDTO dto = hazardService.getDetail(id);
        return Result.success(dto);
    }

    @Operation(summary = "上报隐患")
    @PostMapping("/report")
    public Result<Void> report(@Valid @RequestBody HazardReportDTO dto, @RequestAttribute("userId") Long userId) {
        hazardService.reportHazard(dto, userId);
        return Result.success();
    }

    @Operation(summary = "分配整改任务")
    @PostMapping("/assign")
    public Result<Void> assign(@Valid @RequestBody AssignDTO dto, @RequestAttribute("userId") Long operatorId) {
        hazardService.assignHazard(dto.getHazardId(), dto.getRectifierId(), operatorId, dto.getComment());
        return Result.success();
    }

    @Operation(summary = "更新整改状态")
    @PostMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusUpdateDTO dto, @RequestAttribute("userId") Long userId) {
        hazardService.updateStatus(dto.getHazardId(), dto.getStatus(), dto.getComment(), userId);
        return Result.success();
    }

    @Operation(summary = "统计信息")
    @GetMapping("/stats")
    public Result<HazardStatsDTO> stats() {
        HazardStatsDTO stats = hazardService.getStatistics();
        return Result.success(stats);
    }

    @Operation(summary = "删除隐患")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hazardService.removeById(id);
        return Result.success();
    }
}