package com.mutil.userful.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.MmallProductMapper;
import com.mutil.userful.domain.MmallProduct;
import com.mutil.userful.domain.MmallProductWithBLOBs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhu.liang.common.requestparam.product.MgAddProductRequest;

import java.util.Date;
import java.util.List;

@Service
public class MgProductService {

    @Autowired
    private MmallProductMapper mmallProductMapper;

    /**
     * 获取产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse productList(Integer pageNum,Integer pageSize){
        MmallProduct selectMmproduct = new MmallProduct();
        PageHelper.startPage(pageNum,pageSize);
        List<MmallProduct> list = mmallProductMapper.selectByMmallProduct(selectMmproduct);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 查询产品
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse productSearch(String productName,
                                        Integer productId,
                                        Integer pageNum,
                                        Integer pageSize){
        MmallProduct selectMmproduct = new MmallProduct();
        selectMmproduct.setId(productId);
        selectMmproduct.setName(productName);
        PageHelper.startPage(pageNum,pageSize);
        List<MmallProduct> list = mmallProductMapper.selectByMmallProduct(selectMmproduct);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 查询产品详情
     * @param productId
     * @return
     */
    public ServerResponse productDetail(Integer productId){
        MmallProduct selectMmproduct = new MmallProduct();
        selectMmproduct.setId(productId);
        List<MmallProduct> list = mmallProductMapper.selectByMmallProduct(selectMmproduct);
        return ServerResponse.createBySuccess(list.isEmpty()?null:list.get(0));
    }

    /**
     * 产品Id，上下架状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse setSaleStatus(Integer productId,Integer status){
        MmallProductWithBLOBs updateMmallProduct = new MmallProductWithBLOBs();
        updateMmallProduct.setId(productId);
        updateMmallProduct.setStatus(status);
        if(mmallProductMapper.updateByPrimaryKeySelective(updateMmallProduct)==0){
            return ServerResponse.createByErrorMessage("修改产品状态失败！");
        }
        return ServerResponse.createBySuccessMessage("修改产品状态成功！");
    }

    /**
     * 新增或更新产品
     * @param mgAddProductRequest
     * @return
     */
    public ServerResponse addorupdate(MgAddProductRequest mgAddProductRequest){
        MmallProductWithBLOBs insertProduct = new MmallProductWithBLOBs();
        BeanUtils.copyProperties(mgAddProductRequest,insertProduct);
        int count;
        if(mgAddProductRequest.getId()==null){
            insertProduct.setCreateTime(new Date());
            insertProduct.setUpdateTime(new Date());
            count = mmallProductMapper.insertSelective(insertProduct);
        }else{
            insertProduct.setUpdateTime(new Date());
            count = mmallProductMapper.updateByPrimaryKeySelective(insertProduct);
        }
        if(0==count){
            return ServerResponse.createByErrorMessage("新增或更新产品信息失败！");
        }
        return ServerResponse.createBySuccessMessage("新增或更新产品成功！");
    }


}
