package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.exception.BizException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
  @PostMapping("/upload")
  public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BizException("BIZ_BAD_REQUEST", "上传文件不能为空");
    }

    String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
    String ext = "";
    int dot = originalName.lastIndexOf('.');
    if (dot >= 0 && dot < originalName.length() - 1) {
      ext = "." + originalName.substring(dot + 1);
    }

    String url = "https://mock-upload.offerbridge.local/" + UUID.randomUUID() + ext;
    return ApiResponse.ok(Map.of("url", url));
  }
}
