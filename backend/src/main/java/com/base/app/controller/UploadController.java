package com.base.app.controller;

import com.base.app.config.AuthContext;
import com.base.app.dto.ApiResponse;
import com.base.app.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "Upload", description = "이미지 업로드 (로그인 필요)")
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @Operation(summary = "이미지 업로드")
    @PostMapping
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        AuthContext.requireUserId();
        String url = uploadService.storeImage(file);
        return ApiResponse.success("업로드되었습니다.", Map.of("url", url));
    }
}
