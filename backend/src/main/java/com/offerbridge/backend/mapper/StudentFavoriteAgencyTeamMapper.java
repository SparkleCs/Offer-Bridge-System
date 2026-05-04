package com.offerbridge.backend.mapper;

import com.offerbridge.backend.dto.StudentDtos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentFavoriteAgencyTeamMapper {
  int insertIgnore(@Param("userId") Long userId, @Param("teamId") Long teamId);
  int deleteOne(@Param("userId") Long userId, @Param("teamId") Long teamId);
  int exists(@Param("userId") Long userId, @Param("teamId") Long teamId);
  List<Long> listTeamIdsByUserId(@Param("userId") Long userId);
  StudentDtos.FavoriteAgencyTeamItem findVisibleTeam(@Param("teamId") Long teamId);
  List<StudentDtos.FavoriteAgencyTeamItem> listVisibleFavorites(@Param("userId") Long userId);
}
