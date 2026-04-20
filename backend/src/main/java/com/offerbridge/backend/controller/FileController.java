package com.offerbridge.backend.controller;

import com.offerbridge.backend.common.ApiResponse;
import com.offerbridge.backend.config.AppProperties;
import com.offerbridge.backend.exception.BizException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
  private final AppProperties appProperties;

  public FileController(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

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

    String dir = appProperties.getUpload().getLocalDir();
    if (dir == null || dir.isBlank()) {
      dir = "uploads";
    }
    Path root = Paths.get(dir).toAbsolutePath().normalize();
    String fileName = UUID.randomUUID() + ext;
    Path target = root.resolve(fileName);
    try {
      Files.createDirectories(root);
      try (InputStream in = file.getInputStream()) {
        Files.copy(in, target);
      }
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "文件上传失败");
    }

    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
      .path("/uploads/")
      .path(fileName)
      .toUriString();
    return ApiResponse.ok(Map.of("url", url));
  }
}
