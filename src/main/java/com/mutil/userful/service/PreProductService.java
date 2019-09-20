package com.mutil.userful.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ResponseCode;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.MmallCategoryMapper;
import com.mutil.userful.dao.MmallProductMapper;
import com.mutil.userful.domain.MmallCategory;
import com.mutil.userful.domain.MmallProduct;
import com.mutil.userful.domain.requestparam.product.PreProductRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreProductService {

    @Autowired
    private MmallProductMapper mmallProductMapper;
    @Autowired
    private MgCategoryService mgCategoryService;
    @Autowired
    private MmallCategoryMapper mmallCategoryMapper;

    public ServerResponse productDetail(Integer productId){
        MmallProduct mmallProduct = mmallProductMapper.selectByPrimaryKey(productId);
        if(null==mmallProduct){
            return ServerResponse.createByErrorMessage("未找到对应产品");
        }
        return ServerResponse.createBySuccess(mmallProduct);
    }

    public ServerResponse productList(PreProductRequest preProductRequest){
        if(null==preProductRequest.getCategoryId() && StringUtils.isBlank(preProductRequest.getKeyword())){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        MmallCategory mmallCategory = null;
        List<Integer> categoryIds = Lists.newArrayList();
        if(null!=preProductRequest.getCategoryId()){
            mmallCategory = mmallCategoryMapper.selectByPrimaryKey(preProductRequest.getCategoryId());
        }
        if(null==mmallCategory && StringUtils.isBlank(preProductRequest.getKeyword())){
            PageHelper.startPage(preProductRequest.getPageNum(),preProductRequest.getPageSize());
            List<MmallProduct> productList = Lists.newArrayList();
            PageInfo pageInfo = new PageInfo(productList);
            return ServerResponse.createBySuccess(pageInfo);
        }
        if(null!=mmallCategory){
            categoryIds = (List)mgCategoryService.getAllCategoryIds(preProductRequest.getCategoryId()).getData();
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(preProductRequest.getKeyword())){
            preProductRequest.setKeyword(new StringBuilder().append("%").append(preProductRequest.getKeyword()).append("%").toString());
        }
        // 查询 符合条件的数据
        PageHelper.startPage(preProductRequest.getPageNum(),preProductRequest.getPageSize());
        if(org.apache.commons.lang3.StringUtils.isNotBlank(preProductRequest.getOrderBy())){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(preProductRequest.getOrderBy())){
                String[] orderByArray = preProductRequest.getOrderBy().split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<MmallProduct> productList =
                mmallProductMapper.selectBykeyAndCategoryIds(StringUtils.isBlank(preProductRequest.getKeyword())?null:preProductRequest.getKeyword(),
                                                             categoryIds==null?null:categoryIds);
        return ServerResponse.createBySuccess(productList);
    }


}
