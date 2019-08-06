package com.mutil.userful.dao;

import com.mutil.userful.domain.Userful;

public interface UserfulMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Userful record);

    int insertSelective(Userful record);

    Userful selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Userful record);

    int updateByPrimaryKey(Userful record);
}