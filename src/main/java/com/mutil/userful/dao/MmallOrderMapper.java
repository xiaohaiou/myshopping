package com.mutil.userful.dao;

import com.mutil.userful.domain.MmallOrder;

import java.util.List;

public interface MmallOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmallOrder record);

    int insertSelective(MmallOrder record);

    MmallOrder selectByPrimaryKey(Integer id);

    List<MmallOrder> selectOrderBySelectorBean(MmallOrder mmallOrder);

    int updateByPrimaryKeySelective(MmallOrder record);

    int updateByPrimaryKey(MmallOrder record);
}