package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.MgCategoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zhu.liang.common.requestparam.category.MgAddCategoryRequest;
import zhu.liang.common.util.HibernateValidatorUtil;
import zhu.liang.common.util.ValidateResult;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/manage/category")
@Api(tags = "mgCategory", description = "（0.0.1 初始版本）后台商品类别模块")
public class MgCategoryController {

    @Autowired
    private MgCategoryService mgCategoryService;

    @ApiOperation(value="查询分类", notes="查询分类信息")
    @RequestMapping(value="/getCategory/{categoryId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "类别Id", required = true, paramType = "path"),
    })
    public ServerResponse getCategory(@PathVariable(value = "categoryId", required = true)Integer categoryId, HttpSession session){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        if(!mgCategoryService.checkAdmin(mmallUser).isSuccess()){
            return ServerResponse.createByErrorMessage("不是管理员，无访问权限！");
        }
        List list = mgCategoryService.getCategory(categoryId);
        return ServerResponse.createBySuccess(list);
    }

    @ApiOperation(value="添加分类", notes="添加分类信息")
    @RequestMapping(value="/addCategory",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse addCategory(@RequestBody @ApiParam(name = "addCategoryRequest", value = "addCategoryRequest", required = true) MgAddCategoryRequest addCategoryRequest,
                                      HttpSession session){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        if(!mgCategoryService.checkAdmin(mmallUser).isSuccess()){
            return ServerResponse.createByErrorMessage("不是管理员，无访问权限！");
        }
        // 参数效验
        ValidateResult validateResult = HibernateValidatorUtil.validator(addCategoryRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return mgCategoryService.addCategory(addCategoryRequest);
    }

    @ApiOperation(value="修改分类名称", notes="修改分类信息名称")
    @RequestMapping(value="/changeCategory/{categoryId}/{categoryName}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "类别Id", required = true, paramType = "path"),
            @ApiImplicitParam(name = "categoryName", value = "类别名称", required = true, paramType = "path"),
    })
    public ServerResponse changeCategory(@PathVariable("categoryId") Integer categoryId,
                                         @PathVariable("categoryName") String categoryName,
                                         HttpSession session){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        if(!mgCategoryService.checkAdmin(mmallUser).isSuccess()){
            return ServerResponse.createByErrorMessage("不是管理员，无访问权限！");
        }
        return mgCategoryService.changeCategory(categoryId,categoryName);
    }

    @ApiOperation(value="查询所有子分类", notes="查询当前ID下所有子分类ID")
    @RequestMapping(value="/getAllCategoryIDs/{categoryId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "类别Id", required = true, paramType = "path"),
    })
    public ServerResponse getAllCategoryIDs(@PathVariable(value = "categoryId", required = true)Integer categoryId, HttpSession session){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        if(!mgCategoryService.checkAdmin(mmallUser).isSuccess()){
            return ServerResponse.createByErrorMessage("不是管理员，无访问权限！");
        }
        return mgCategoryService.getAllCategoryIds(categoryId);
    }

}
