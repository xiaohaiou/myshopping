package com.mutil.userful.dao;

import com.mutil.userful.domain.MmallShipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MmallShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserIdAndPrimaryId(@Param("userId")Integer userId,@Param("id")Integer id);

    int insert(MmallShipping record);

    int insertSelective(MmallShipping record);

    MmallShipping selectByPrimaryKey(Integer id);

    List<MmallShipping> selectByShipBean(MmallShipping shipping);

    int updateByPrimaryKeySelective(MmallShipping record);

    int updateByPrimaryKey(MmallShipping record);
}