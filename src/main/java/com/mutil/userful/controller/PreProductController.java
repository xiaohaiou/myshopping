package com.mutil.userful.controller;

import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.requestparam.ValidateResult;
import com.mutil.userful.domain.requestparam.product.PreProductRequest;
import com.mutil.userful.service.PreProductService;
import com.mutil.userful.util.ValidatorUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/pre/product")
@Api(tags = "preProduct", description = "（0.0.1 初始版本）前台产品模块")
public class PreProductController {
	
	private static final Logger log = LoggerFactory.getLogger(PreProductController.class);

    @Autowired
    private PreProductService preProductService;

    @ApiOperation(value="查询产品详细信息", notes="查询产品详细信息")
    @RequestMapping(value="/detail.do/{productId}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品Id", required = true, paramType = "path"),
    })
    public ServerResponse getCategory(@PathVariable(value = "productId", required = true)Integer productId){
        if(null==productId){
            return ServerResponse.createByError();
        }
        return preProductService.productDetail(productId);
    }

    @ApiOperation(value="查询产品列表", notes="查询产品列表")
    @RequestMapping(value="/list.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse list(@RequestBody PreProductRequest preProductRequest){
        ValidateResult validateResult = ValidatorUtil.validator(preProductRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return preProductService.productList(preProductRequest);
    }




}
