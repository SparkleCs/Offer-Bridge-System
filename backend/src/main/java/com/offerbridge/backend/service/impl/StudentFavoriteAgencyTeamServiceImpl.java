package com.offerbridge.backend.service.impl;

import com.offerbridge.backend.dto.StudentDtos;
import com.offerbridge.backend.entity.UserAccount;
import com.offerbridge.backend.exception.BizException;
import com.offerbridge.backend.mapper.StudentFavoriteAgencyTeamMapper;
import com.offerbridge.backend.mapper.UserAccountMapper;
import com.offerbridge.backend.service.StudentFavoriteAgencyTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentFavoriteAgencyTeamServiceImpl implements StudentFavoriteAgencyTeamService {
  private final StudentFavoriteAgencyTeamMapper favoriteAgencyTeamMapper;
  private final UserAccountMapper userAccountMapper;

  public StudentFavoriteAgencyTeamServiceImpl(StudentFavoriteAgencyTeamMapper favoriteAgencyTeamMapper,
                                              UserAccountMapper userAccountMapper) {
    this.favoriteAgencyTeamMapper = favoriteAgencyTeamMapper;
    this.userAccountMapper = userAccountMapper;
  }

  @Override
  public List<StudentDtos.FavoriteAgencyTeamItem> listFavorites(Long userId) {
    requireStudent(userId);
    return favoriteAgencyTeamMapper.listVisibleFavorites(userId);
  }

  @Override
  @Transactional
  public StudentDtos.FavoriteAgencyTeamItem addFavorite(Long userId, Long teamId) {
    requireStudent(userId);
    StudentDtos.FavoriteAgencyTeamItem item = requireVisibleTeam(teamId);
    favoriteAgencyTeamMapper.insertIgnore(userId, teamId);
    item.setFavorited(true);
    return item;
  }

  @Override
  @Transactional
  public StudentDtos.FavoriteAgencyTeamItem removeFavorite(Long userId, Long teamId) {
    requireStudent(userId);
    favoriteAgencyTeamMapper.deleteOne(userId, teamId);
    StudentDtos.FavoriteAgencyTeamItem item = favoriteAgencyTeamMapper.findVisibleTeam(teamId);
    if (item == null) {
      item = new StudentDtos.FavoriteAgencyTeamItem();
      item.setTeamId(teamId);
    }
    item.setFavorited(false);
    return item;
  }

  private StudentDtos.FavoriteAgencyTeamItem requireVisibleTeam(Long teamId) {
    StudentDtos.FavoriteAgencyTeamItem item = favoriteAgencyTeamMapper.findVisibleTeam(teamId);
    if (item == null) {
      throw new BizException("BIZ_NOT_FOUND", "团队不存在或不可收藏");
    }
    return item;
  }

  private void requireStudent(Long userId) {
    if (userId == null) {
      throw new BizException("BIZ_UNAUTHORIZED", "未登录或登录已过期");
    }
    UserAccount user = userAccountMapper.findById(userId);
    if (user == null || !"ACTIVE".equals(user.getStatus())) {
      throw new BizException("BIZ_UNAUTHORIZED", "登录状态已失效，请重新登录");
    }
    if (!"STUDENT".equals(user.getRole())) {
      throw new BizException("BIZ_FORBIDDEN", "仅学生可收藏中介团队");
    }
  }
}
