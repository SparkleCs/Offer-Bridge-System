package com.offerbridge.backend.mapper;

import com.offerbridge.backend.entity.AgencyInvitation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgencyInvitationMapper {
  int insertOne(AgencyInvitation entity);
  AgencyInvitation findByToken(@Param("token") String token);
  int markAccepted(@Param("id") Long id, @Param("userId") Long userId);
}
