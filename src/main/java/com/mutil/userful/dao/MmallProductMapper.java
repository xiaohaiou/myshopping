package com.mutil.userful.dao;

import com.mutil.userful.domain.MmallProduct;
import com.mutil.userful.domain.MmallProductWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MmallProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmallProductWithBLOBs record);

    int insertSelective(MmallProductWithBLOBs record);

    MmallProductWithBLOBs selectByPrimaryKey(Integer id);

    List<MmallProduct> selectBykeyAndCategoryIds(@Param("productName")String productName,
                                                 @Param("categoryIdList")List<Integer> categoryIdList);

    List<MmallProduct> selectByMmallProduct(MmallProduct mmallProduct);

    int updateByPrimaryKeySelective(MmallProductWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(MmallProductWithBLOBs record);

    int updateByPrimaryKey(MmallProduct record);
}