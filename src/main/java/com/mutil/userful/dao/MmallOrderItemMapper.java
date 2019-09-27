package com.mutil.userful.dao;

import com.mutil.userful.domain.MmallOrderItem;

import java.util.List;

public interface MmallOrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmallOrderItem record);

    int insertSelective(MmallOrderItem record);

    int insertBatch(List<MmallOrderItem> list);

    MmallOrderItem selectByPrimaryKey(Integer id);

    List<MmallOrderItem> selectByMmallOrderItem(MmallOrderItem mmallOrderItem);

    int updateByPrimaryKeySelective(MmallOrderItem record);

    int updateByPrimaryKey(MmallOrderItem record);
}