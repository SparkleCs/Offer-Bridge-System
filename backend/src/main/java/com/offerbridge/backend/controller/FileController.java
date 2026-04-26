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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
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
  public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(value = "bucket", required = false) String bucket) {
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
    String normalizedBucket = normalizeBucket(bucket);
    String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    Path root = Paths.get(dir).toAbsolutePath().normalize();
    String fileName = UUID.randomUUID() + ext;
    Path target = root.resolve(normalizedBucket).resolve(ym).resolve(fileName);
    try {
      Files.createDirectories(target.getParent());
      try (InputStream in = file.getInputStream()) {
        Files.copy(in, target);
      }
    } catch (Exception ex) {
      throw new BizException("BIZ_INTERNAL_ERROR", "文件上传失败");
    }

    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
      .path("/uploads/")
      .path(normalizedBucket + "/")
      .path(ym + "/")
      .path(fileName)
      .toUriString();
    return ApiResponse.ok(Map.of("url", url));
  }

  private String normalizeBucket(String bucket) {
    if (bucket == null || bucket.isBlank()) {
      return "general";
    }
    String value = bucket.trim().toLowerCase();
    Set<String> allowed = Set.of("org-verification", "member-verification", "student-verification", "avatar", "general", "chat");
    if (!allowed.contains(value)) {
      throw new BizException("BIZ_BAD_REQUEST", "不支持的上传桶");
    }
    return value;
  }
}
