package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.SampleDto;
import com.base.app.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sample", description = "샘플 CRUD API (구조 참고용, 실제 도메인 API로 교체하세요)")
@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @Operation(summary = "목록 조회")
    @GetMapping
    public ApiResponse<List<SampleDto>> list() {
        return ApiResponse.success(sampleService.findAll());
    }

    @Operation(summary = "단건 조회")
    @GetMapping("/{id}")
    public ApiResponse<SampleDto> detail(@Parameter(description = "샘플 ID") @PathVariable Long id) {
        return ApiResponse.success(sampleService.findById(id));
    }

    @Operation(summary = "등록")
    @PostMapping
    public ApiResponse<SampleDto> create(@RequestBody SampleDto sampleDto) {
        return ApiResponse.success("생성되었습니다", sampleService.create(sampleDto));
    }

    @Operation(summary = "수정")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@Parameter(description = "샘플 ID") @PathVariable Long id,
                                     @RequestBody SampleDto sampleDto) {
        sampleService.update(id, sampleDto);
        return ApiResponse.success("수정되었습니다", null);
    }

    @Operation(summary = "삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "샘플 ID") @PathVariable Long id) {
        sampleService.delete(id);
        return ApiResponse.success("삭제되었습니다", null);
    }

}
