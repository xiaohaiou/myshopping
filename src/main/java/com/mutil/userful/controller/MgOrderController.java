package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.MgOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/manage/order")
@Api(tags = "mgOrder", description = "（0.0.1 初始版本）后台订单模块")
public class MgOrderController {

    private static final Logger log = LoggerFactory.getLogger(MgOrderController.class);

    @Autowired
    private MgOrderService mgOrderService;

    @ApiOperation(value="订单列表", notes="订单列表")
    @RequestMapping(value="/list.do/{pageNum}/{pageSize}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数", required = true, paramType = "path"),
    })
    public ServerResponse list(HttpSession session,
                               @PathVariable("pageNum")Integer pageNum,
                               @PathVariable("pageSize")Integer pageSize){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return mgOrderService.list(pageNum,pageSize);
    }

    @ApiOperation(value="查询订单", notes="查询订单")
    @RequestMapping(value="/orderInfo.do/{orderNo}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse info(HttpSession session,
                               @PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return mgOrderService.order(orderNo);
    }

    @ApiOperation(value="订单发货", notes="订单发货")
    @RequestMapping(value="/sendGoods.do/{orderNo}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse sendgoods(HttpSession session,
                               @PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return mgOrderService.sendGoods(orderNo);
    }


}
