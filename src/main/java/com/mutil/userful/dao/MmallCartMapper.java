package com.mutil.userful.dao;

import com.mutil.userful.annotation.In;
import com.mutil.userful.domain.MmallCart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MmallCartMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList")List<String> productIdList);
    
    int insert(MmallCart record);

    int insertSelective(MmallCart record);

    MmallCart selectByPrimaryKey(Integer id);

    List<MmallCart> selectCartsByUseId(@Param("userId") Integer userId);

    List<MmallCart> selectCartByCart(MmallCart mmallCart);

    int selectCartNum(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int updateByPrimaryKeySelective(MmallCart record);

    int updateForSelectOrUnSelectAll(@Param("userId")Integer userId,@Param("checked")Integer checked);

    int updateByPrimaryKey(MmallCart record);
}