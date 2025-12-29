package com.zhu.mapper;

import com.zhu.pojo.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 */
@Mapper
public interface RoleMapper {
    
    int insert(Role role);
    
    int deleteByPrimaryKey(Integer id);
    
    int updateByPrimaryKey(Role role);
    
    Role selectByPrimaryKey(Integer id);
    
    Role selectByRoleCode(@Param("roleCode") String roleCode);
    
    List<Role> selectAll();
}



