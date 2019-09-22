package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.PreCartService;
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
@RequestMapping(value = "/pre/cart")
@Api(tags = "preCart", description = "（0.0.1 初始版本）前台购物车模块")
public class PreCartController {

    private static final Logger log = LoggerFactory.getLogger(PreCartController.class);

    @Autowired
    private PreCartService preCartService;

    @ApiOperation(value="购物车列表", notes="购物车列表")
    @RequestMapping(value="/list.do/{userId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, paramType = "path"),
    })
    public ServerResponse list(HttpSession session,
                               @PathVariable("userId")Integer userId){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preCartService.cartList(userId);
    }

    @ApiOperation(value="购物车添加商品", notes="添加商品到购物车")
    @RequestMapping(value="/add.do/{productId}/{count}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "count", value = "数量", required = true, paramType = "path"),
    })
    public ServerResponse add(HttpSession session,
                              @PathVariable("productId")Integer productId,
                              @PathVariable("count")Integer count){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(null==productId || null==count){
            return ServerResponse.createByErrorMessage("参数有误，请重新输入！");
        }
        return preCartService.add(user.getId(),productId,count);
    }

    @ApiOperation(value="购物车更新商品数量", notes="更新购物车商品数量")
    @RequestMapping(value="/update.do/{productId}/{count}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "count", value = "数量", required = true, paramType = "path"),
    })
    public ServerResponse update(HttpSession session,
                                 @PathVariable("productId")Integer productId,
                                 @PathVariable("count")Integer count){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(null==productId || null==count){
            return ServerResponse.createByErrorMessage("参数有误，请重新输入！");
        }
        return preCartService.update(user.getId(),productId,count);
    }

    @ApiOperation(value="移除购物车商品", notes="移除购物车商品")
    @RequestMapping(value="/removeProduct.do/{productId}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID，英文逗号隔开", required = true, paramType = "path"),
    })
    public ServerResponse removeProduct(HttpSession session,
                                        @PathVariable("productId")String productId){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(null==productId){
            return ServerResponse.createByErrorMessage("参数有误，请重新输入！");
        }
        return preCartService.reomveProduct(user.getId(),productId);
    }

    @ApiOperation(value="购物车选中某个商品", notes="购物车选中某个商品")
    @RequestMapping(value="/selectProduct.do/{productId}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true, paramType = "path"),
    })
    public ServerResponse selectProduct(HttpSession session,
                                        @PathVariable("productId")Integer productId){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(null==productId){
            return ServerResponse.createByErrorMessage("参数有误，请重新输入！");
        }
        return preCartService.selectProduct(user.getId(),productId);
    }

    @ApiOperation(value="查询购物车产品数量", notes="查询购物车产品数量")
    @RequestMapping(value="/getProductCount.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse getProductCount(HttpSession session){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preCartService.getProductCount(user.getId());
    }

    @ApiOperation(value="购物车产品全选", notes="购物车全选")
    @RequestMapping(value="/selectAll.do",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preCartService.selectAll(user.getId(),Const.Cart.CHECKED);
    }

    @ApiOperation(value="购物车产品不全选", notes="购物车不全选")
    @RequestMapping(value="/unSelectAll.do",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preCartService.selectAll(user.getId(),Const.Cart.UN_CHECKED);
    }

}
