package com.mutil.userful.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.*;
import com.mutil.userful.domain.MmallOrder;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MgOrderService {

    private static final Logger log = LoggerFactory.getLogger(MgOrderService.class);

    @Autowired
    private MmallOrderMapper mmallOrderMapper;
    @Autowired
    private MmallOrderItemMapper mmallOrderItemMapper;

    /**
     * 获取订单列表信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse list(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<MmallOrder> mmallOrderList = mmallOrderMapper.selectOrderBySelectorBean(new MmallOrder());
        PageInfo pageInfo = new PageInfo(mmallOrderList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 查询订单信息
     * @param orderNo
     * @return
     */
    public ServerResponse order(Long orderNo){
        MmallOrder selectMmallOrder = new MmallOrder();
        selectMmallOrder.setOrderNo(orderNo);
        List<MmallOrder> mmallOrderList = mmallOrderMapper.selectOrderBySelectorBean(new MmallOrder());
        if(CollectionUtils.isEmpty(mmallOrderList)){
            return ServerResponse.createByErrorMessage("未查到对应订单信息！");
        }
        return ServerResponse.createBySuccess(mmallOrderList.get(0));
    }

    /**
     * 订单发货
     * @param orderNo
     * @return
     */
    public ServerResponse sendGoods(Long orderNo){
        MmallOrder selectMmallOrder = new MmallOrder();
        selectMmallOrder.setOrderNo(orderNo);
        List<MmallOrder> mmallOrderList = mmallOrderMapper.selectOrderBySelectorBean(new MmallOrder());
        if(CollectionUtils.isEmpty(mmallOrderList)){
            return ServerResponse.createByErrorMessage("未查到对应订单信息！");
        }

        MmallOrder order = mmallOrderList.get(0);
        if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());
            mmallOrderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccess("发货成功");
        }
        return ServerResponse.createByErrorMessage("发货失败！");
    }
}
