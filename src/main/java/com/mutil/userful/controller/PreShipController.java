package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.PreShipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zhu.liang.common.requestparam.ship.PreAddShipRequest;
import zhu.liang.common.util.HibernateValidatorUtil;
import zhu.liang.common.util.ValidateResult;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/pre/ship")
@Api(tags = "preShip", description = "（0.0.1 初始版本）前台收货地址模块")
public class PreShipController {

    private static final Logger log = LoggerFactory.getLogger(PreShipController.class);

    @Autowired
    private PreShipService preShipService;

    @ApiOperation(value="新增或更新收货地址", notes="新增或更新收货地址")
    @RequestMapping(value="/addorupdate.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse addorupdate(HttpSession session,
                                      @RequestBody PreAddShipRequest mgAddProductRequest){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        // 参数效验
        ValidateResult validateResult = HibernateValidatorUtil.validator(mgAddProductRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return preShipService.addorupdate(mgAddProductRequest,user.getId());
    }

    @ApiOperation(value="删除收货地址", notes="删除收货地址")
    @RequestMapping(value="/delete.do/{id}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "收货地址ID", required = true, paramType = "path"),
    })
    public ServerResponse delete(HttpSession session,
                                 @PathVariable(value = "id")Integer id){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(null==id){
            return ServerResponse.createByErrorMessage("地址ID有误！");
        }
        return preShipService.delete(user.getId(),id);
    }

    @ApiOperation(value="收货地址列表", notes="收货地址列表")
    @RequestMapping(value="/list.do/{pageNum}/{pageSize}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, paramType = "path"),
            @ApiImplicitParam(name = "id", value = "每页显示条数", required = true, paramType = "path"),
    })
    public ServerResponse delete(HttpSession session,
                                 @PathVariable(value = "pageNum")Integer pageNum,
                                 @PathVariable(value = "pageSize")Integer pageSize){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return preShipService.list(user.getId(),pageNum,pageSize);
    }


}
