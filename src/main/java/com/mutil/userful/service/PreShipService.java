package com.mutil.userful.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.MmallShippingMapper;
import com.mutil.userful.domain.MmallShipping;
import com.mutil.userful.domain.requestparam.ship.PreAddShipRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PreShipService {

    private static final Logger log = LoggerFactory.getLogger(PreShipService.class);

    @Autowired
    private MmallShippingMapper mmallShippingMapper;

    /**
     * 添加或者更新收货地址
     * @param preAddShipRequest
     * @param userId
     * @return
     */
    public ServerResponse addorupdate(PreAddShipRequest preAddShipRequest,Integer userId){
        MmallShipping insertShip = new MmallShipping();
        final Date nowTime = new Date();
        insertShip.setUserId(userId);
        BeanUtils.copyProperties(preAddShipRequest,insertShip);
        if(insertShip.getId()==null){
            insertShip.setCreateTime(nowTime);
            insertShip.setUpdateTime(nowTime);
            if(mmallShippingMapper.insertSelective(insertShip)==0){
                return ServerResponse.createByErrorMessage("插入收货地址失败！");
            }
        }else{
            insertShip.setUpdateTime(nowTime);
            if(mmallShippingMapper.updateByPrimaryKeySelective(insertShip)==0){
                return ServerResponse.createByErrorMessage("更新收货地址失败！");
            }
        }
        return ServerResponse.createBySuccessMessage("更新收货地址成功！");
    }

    /**
     * 删除收货地址
     * @param userId
     * @param id
     * @return
     */
    public ServerResponse delete(Integer userId,Integer id){
        if(mmallShippingMapper.deleteByUserIdAndPrimaryId(userId,id)==0){
            return ServerResponse.createByErrorMessage("地址未删除！");
        }
        return ServerResponse.createBySuccessMessage("删除收货地址成功！");
    }

    /**
     * 查询收货地址列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<MmallShipping> shippingList = mmallShippingMapper.selectByShipBean(new MmallShipping());
        PageInfo<MmallShipping> page = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(page);
    }


}
