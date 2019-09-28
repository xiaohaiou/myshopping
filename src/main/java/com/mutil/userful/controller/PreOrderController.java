package com.mutil.userful.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.PreOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping(value = "/pre/order")
@Api(tags = "preOrder", description = "（0.0.1 初始版本）前台订单模块")
public class PreOrderController {

    private static final Logger log = LoggerFactory.getLogger(PreOrderController.class);

    @Autowired
    private PreOrderService preOrderService;

    @ApiOperation(value="支付接口", notes="订单支付")
    @RequestMapping(value="/pay.do/{orderNo}",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse pay(HttpSession session,@PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }else if(null==orderNo){
            return ServerResponse.createByErrorMessage("输入订单号无效！");
        }
        return preOrderService.pay(user.getId(),orderNo);
    }

    @ApiOperation(value="支付宝回调函数", notes="回调函数")
    @PostMapping(value="/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }
        ServerResponse serverResponse = preOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @ApiOperation(value="查询订单状态", notes="查询订单的状态")
    @RequestMapping(value="/status.do/{orderNo}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse orderStatus(HttpSession session,@PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }else if(null==orderNo){
            return ServerResponse.createByErrorMessage("输入订单号无效！");
        }
        return preOrderService.orderStatus(user.getId(),orderNo);
    }

    @ApiOperation(value="创建订单", notes="创建订单")
    @RequestMapping(value="/create.do/{shippingId}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shippingId", value = "地址Id", required = true, paramType = "path"),
    })
    public ServerResponse create(HttpSession session,@PathVariable("shippingId")Integer shippingId){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }else if(null==shippingId){
            return ServerResponse.createByErrorMessage("输入收货地址无效！");
        }
        return preOrderService.create(user.getId(),shippingId);
    }

    @ApiOperation(value="获取订单商品信息", notes="获取订单商品信息")
    @RequestMapping(value="/product.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse product(HttpSession session){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preOrderService.product(user.getId());
    }

    @ApiOperation(value="订单列表", notes="订单列表")
    @RequestMapping(value="/list.do/{pageSize}/{pageNum}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "每页显示数据", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, paramType = "path"),
    })
    public ServerResponse pay(HttpSession session,
                              @PathVariable("pageSize")Integer pageSize,
                              @PathVariable("pageNum")Integer pageNum){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preOrderService.getOrders(user.getId(),pageNum,pageSize);
    }

    @ApiOperation(value="订单详情", notes="订单详情")
    @RequestMapping(value="/detail.do/{orderNo}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse detail(HttpSession session,
                                 @PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preOrderService.detail(user.getId(),orderNo);
    }

    @ApiOperation(value="取消订单", notes="取消订单")
    @RequestMapping(value="/cancel.do/{orderNo}",method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = true, paramType = "path"),
    })
    public ServerResponse cancel(HttpSession session,
                                 @PathVariable("orderNo")Long orderNo){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preOrderService.cancel(user.getId(),orderNo);
    }

}
