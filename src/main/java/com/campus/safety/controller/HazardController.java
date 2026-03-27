package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.HazardDTO;
import com.campus.safety.dto.HazardUpdateDTO;
import com.campus.safety.entity.Hazard;
import com.campus.safety.entity.User;
import com.campus.safety.service.HazardService;
import com.campus.safety.service.UserService;
import com.campus.safety.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hazard")
@RequiredArgsConstructor
public class HazardController {
    
    private final HazardService hazardService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping
    public Result<Void> create(@RequestBody @Validated HazardDTO dto) {
        hazardService.create(dto);
        return Result.success();
    }
    
    /**
     * ⭐ 上传图片
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = hazardService.uploadImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HazardUpdateDTO dto) {
        hazardService.update(id, dto);
        return Result.success();
    }
    
    /**
     * ⭐ 更新隐患等级（仅管理员）
     */
    @PutMapping("/{id}/level")
    public Result<Void> updateLevel(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String level = params.get("level");
        hazardService.updateLevel(id, level);
        return Result.success();
    }
    
    @RequestMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @RequestParam Long handlerId) {
        hazardService.assign(id, handlerId);
        return Result.success();
    }
    
    @RequestMapping("/{id}/resolve")
    public Result<Void> resolve(@PathVariable Long id, @RequestParam String resolution) {
        hazardService.resolve(id, resolution);
        return Result.success();
    }
    
    @RequestMapping("/{id}/close")
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
    
    /**
     * ⭐ 获取我的任务（维修员查看自己名下的隐患）
     */
    @GetMapping("/my-tasks")
    public Result<List<Hazard>> getMyTasks() {
        Long userId = jwtUtil.getCurrentUserId();
        return Result.success(hazardService.getMyTasks(userId));
    }
    
    /**
     * ⭐ 获取维修员列表
     */
    @GetMapping("/rectifiers")
    public Result<List<User>> getRectifiers() {
        return Result.success(hazardService.getRectifiers());
    }
    
    /**
     * ⭐ 获取处理中的隐患列表
     */
    @GetMapping("/processing")
    public Result<List<Hazard>> getProcessingHazards() {
        return Result.success(hazardService.getProcessingHazards());
    }
    
    /**
     * ⭐ 完成修理（将 PROCESSING 转为 RESOLVED）
     */
    @PostMapping("/{id}/complete")
    public Result<Void> completeRepair(@PathVariable Long id) {
        hazardService.completeRepair(id);
        return Result.success();
    }
    
    /**
     * ⭐ 删除隐患（管理员权限）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHazard(@PathVariable Long id) {
        hazardService.deleteHazard(id);
        return Result.success();
    }
    /**
 * ⭐ 取消分配（管理员将已分配隐患改回待处理）
 */
    @PostMapping("/{id}/cancel-assign")
    public Result<Void> cancelAssign(@PathVariable Long id) {
        hazardService.cancelAssign(id);
        return Result.success();
    }
}