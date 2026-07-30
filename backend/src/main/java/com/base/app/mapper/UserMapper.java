package com.base.app.mapper;

import com.base.app.dto.UserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    UserDto findByEmail(String email);

    UserDto findById(Long id);

    boolean existsByEmail(String email);

    int insert(UserDto userDto);

    List<UserDto> findAll();

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int updateRole(@Param("id") Long id, @Param("role") String role);
}
