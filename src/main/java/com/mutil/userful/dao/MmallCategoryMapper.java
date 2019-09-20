package com.mutil.userful.dao;

import com.mutil.userful.domain.MmallCategory;

import java.util.List;

public interface MmallCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmallCategory record);

    int insertSelective(MmallCategory record);

    MmallCategory selectByPrimaryKey(Integer id);

    List<MmallCategory> selectByMmallCategory(MmallCategory mmallCategory);

    List<Integer> selectCategoryIdByParentId(Integer parentId);

    int updateByPrimaryKeySelective(MmallCategory record);

    int updateByPrimaryKey(MmallCategory record);
}