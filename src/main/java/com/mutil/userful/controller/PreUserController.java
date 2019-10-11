package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.PreUserService;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zhu.liang.common.requestparam.user.PreLoginRequest;
import zhu.liang.common.requestparam.user.PreRegisterRequest;
import zhu.liang.common.requestparam.user.PreUpdateUserInfoRequest;
import zhu.liang.common.util.HibernateValidatorUtil;
import zhu.liang.common.util.ValidateResult;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/pre/user")
@Api(tags = "preUser", description = "（0.0.1 初始版本）前台用户模块")
public class PreUserController {
	
	private static final Logger log = LoggerFactory.getLogger(PreUserController.class);

    @Autowired
    private PreUserService preUserService;

    @ApiOperation(value="用户登入操作", notes="根据用户名和密码进行登入操作")
    @RequestMapping(value="/login.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse<MmallUser> logion(@RequestBody @ApiParam(name = "mmallUser", value = "mmallUser", required = true)PreLoginRequest loginRequest, HttpSession session){
        ValidateResult validateResult = HibernateValidatorUtil.validator(loginRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return preUserService.validateUser(loginRequest,session);
    }

    @ApiOperation(value="注册用户操作", notes="新增用户")
    @RequestMapping(value="/register.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse register(@RequestBody @ApiParam(name = "register", value = "register", required = true) PreRegisterRequest registerRequest){
        ValidateResult validateResult = HibernateValidatorUtil.validator(registerRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return preUserService.registerUser(registerRequest);
    }

    @ApiOperation(value="检查用户名是否有效", notes="用户名和邮箱是否有效")
    @RequestMapping(value="/check.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "str", value = "用户名或email", required = true,paramType="query"),
            @ApiImplicitParam(name = "type", value = "str类型，username或者email", required = true,paramType="query"),
    })
    public ServerResponse check(@RequestParam (value = "str", required = true)String str,
                                @RequestParam (value = "type", required = true)String type){
        if((!Const.USERNAME.equals(type) && !Const.EMAIL.equals(type)) ||
                StringUtils.isBlank(str)){
            return ServerResponse.createByErrorMessage("参数有误，请检查后重新输入！");
        }
        return preUserService.checkUserParam(str,type);
    }

    @ApiOperation(value="获取当前用户信息", notes="获取当前登入用户信息")
    @RequestMapping(value="/currentUserInfo.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse currentUerInfo(HttpSession session){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        return ServerResponse.createBySuccess(mmallUser);
    }

    @ApiOperation(value="忘记密码，获取问题", notes="忘记密码获取对应的问题")
    @RequestMapping(value="/question.do/{username}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "path"),
    })
    public ServerResponse question(@PathVariable (value = "username", required = true)String username){
        if(StringUtils.isBlank(username)){
            return ServerResponse.createByErrorMessage("用户名不能为空！");
        }
        return preUserService.question(username);
    }

    @ApiOperation(value="检查重置密码问题答案", notes="检查重置密码问题答案，返回token值")
    @RequestMapping(value="/answer.do/{username}/{question}/{answer}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "path"),
            @ApiImplicitParam(name = "question", value = "问题", required = true, paramType = "path"),
            @ApiImplicitParam(name = "answer", value = "答案", required = true, paramType = "path"),
    })
    public ServerResponse answer(@PathVariable (value = "username", required = true)String username,
                                 @PathVariable (value = "question", required = true)String question,
                                 @PathVariable (value = "answer", required = true)String answer){
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(question) ||
                StringUtils.isBlank(answer)){
            return ServerResponse.createByErrorMessage("参数不全!");
        }
        return preUserService.question(username,question,answer);
    }

    @ApiOperation(value="通过问题重置密码", notes="通过返回的token值重置密码")
    @RequestMapping(value="/resetPassword.do/{username}/{passwordNew}/{forgetToken}",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "path"),
            @ApiImplicitParam(name = "passwordNew", value = "新密码", required = true, paramType = "path"),
            @ApiImplicitParam(name = "forgetToken", value = "重置密码token值", required = true, paramType = "path"),
    })
    public ServerResponse resetPassWord(@PathVariable (value = "username", required = true)String username,
                                        @PathVariable (value = "passwordNew", required = true)String passwordNew,
                                        @PathVariable (value = "forgetToken", required = true)String forgetToken){
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(passwordNew) ||
                StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数不全!");
        }
        return preUserService.resetPassWord(username,passwordNew,forgetToken);
    }

    @ApiOperation(value="登入后更新用户信息", notes="登入后更新用户信息")
    @RequestMapping(value="/updateIn.do",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse update(@RequestBody @ApiParam(name = "updateUserInfoRequest", value = "updateUserInfoRequest", required = true)
                                         PreUpdateUserInfoRequest updateUserInfoRequest,
                                HttpSession session ){
        MmallUser mmallUser = (MmallUser) session.getAttribute(Const.CURRENT_USER);
        if(null == mmallUser){
            return ServerResponse.createByErrorMessage("未登入，无访问权限！");
        }
        ValidateResult validateResult = HibernateValidatorUtil.validator(updateUserInfoRequest);
        if(!validateResult.isSuccess()){
            return ServerResponse.createByErrorMessage(Arrays.toString(validateResult.getErrMsg()));
        }
        return preUserService.updateUser(updateUserInfoRequest,mmallUser);
    }

    @ApiOperation(value="退出登入", notes="退出登入")
    @RequestMapping(value="/loginOut.do",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ServerResponse logOut(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }



}
