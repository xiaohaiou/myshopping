package com.mutil.userful.controller;

import com.github.tobato.fastdfs.shop.FastDFSClientUtil;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.MgProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhu.liang.common.requestparam.product.MgAddProductRequest;
import zhu.liang.common.util.HibernateValidatorUtil;
import zhu.liang.common.util.ValidateResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/manage/product")
@Api(tags = "mgProduct", description = "（0.0.1 初始版本）后台产品模块")
public class MgProductController {
	
	private static final Logger log = LoggerFactory.getLogger(MgProductController.class);

    @Autowired
    private MgProductService mgProductService;
    @Autowired
    private FastDFSClientUtil dfsClient;

    @ApiOperation(value="产品列表", notes="产品列表")
    @RequestMapping(value="/list.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页数", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数据", required = true, paramType = "query"),
    })
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return mgProductService.productList(pageNum,pageSize);
    }

    @ApiOperation(value="查询产品", notes="查询产品")
    @RequestMapping(value="/search.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名称", required = false, paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品Id", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数据", required = true, paramType = "query"),
    })
    public ServerResponse search(HttpSession session,
                                 @RequestParam(value = "productName")String productName,
                                 @RequestParam(value = "productId")Integer productId,
                                 @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                 @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        return mgProductService.productSearch(productName,productId,pageNum,pageSize);
    }

    @ApiOperation(value="图片上传", notes="图片上传")
    @RequestMapping(value="/upload.do",headers="content-type=multipart/form-data", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 HttpServletRequest request,
                                 @RequestParam(value = "upload_file",required = false) MultipartFile file){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("不是管理员，无操作权限！");
        }

        try {
            String fileUrl = dfsClient.uploadFile(file);
            request.setAttribute("msg", "成功上传文件，  '" + fileUrl + "'");
            log.info("上传文件成功，路径为："+fileUrl);
            return ServerResponse.createBySuccess(fileUrl);
        } catch (IOException e) {
            log.info("上传文件出错，信息为：{}",e.getMessage());
        }
        return ServerResponse.createByError();
    }

    @ApiOperation(value="查询产品详情", notes="查询产品详情")
    @RequestMapping(value="/detail.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, paramType = "query"),
    })
    public ServerResponse detail(HttpSession session,
                                 @RequestParam(value = "productId")Integer productId){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("不是管理员，无操作权限！");
        }
        return mgProductService.productDetail(productId);
    }

    @ApiOperation(value="产品上下架", notes="产品上下架")
    @RequestMapping(value="/saleStatus.do/{productId}/{status}",method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, paramType = "path"),
    })
    public ServerResponse saleStatus(HttpSession session,
                                     @PathVariable(value = "productId")Integer productId,
                                     @PathVariable(value = "status")Integer status){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("不是管理员，无操作权限！");
        }
        return mgProductService.setSaleStatus(productId,status);
    }

    @ApiOperation(value="新增或更新产品", notes="新增或更新产品")
    @RequestMapping(value="/addorupdate.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse addorupdate(HttpSession session,
                                      @RequestBody MgAddProductRequest mgAddProductRequest){
        MmallUser user = (MmallUser)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorMessage("用户未登入，无访问权限！");
        }
        if(user.getRole()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("不是管理员，无操作权限！");
        }
        // 参数效验
        ValidateResult validateResult = HibernateValidatorUtil.validator(mgAddProductRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return mgProductService.addorupdate(mgAddProductRequest);
    }



}
