package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.AdminDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminReviewMapper {
  List<AdminDtos.ReviewListItem> listOrgReviews();
  List<AdminDtos.ReviewListItem> listMemberReviews();
  List<AdminDtos.ReviewListItem> listStudentReviews();

  AdminDtos.ReviewDetailView findOrgReviewDetail(@Param("userId") Long userId);
  AdminDtos.ReviewDetailView findMemberReviewDetail(@Param("userId") Long userId);
  AdminDtos.ReviewDetailView findStudentReviewDetail(@Param("userId") Long userId);
}

