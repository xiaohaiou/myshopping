package com.mutil.userful.service;

import com.alibaba.druid.util.StringUtils;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.common.TokenCache;
import com.mutil.userful.dao.MmallUserMapper;
import com.mutil.userful.domain.MmallUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhu.liang.common.requestparam.user.PreLoginRequest;
import zhu.liang.common.requestparam.user.PreRegisterRequest;
import zhu.liang.common.requestparam.user.PreUpdateUserInfoRequest;
import zhu.liang.common.util.MD5Util;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Service
public class PreUserService {

    @Autowired
    private MmallUserMapper mmallUserMapper;

    /**
     * 登入操作
     * @param loginRequest 登入用户信息
     * @return
     */
    public ServerResponse validateUser(PreLoginRequest loginRequest, HttpSession session){
        MmallUser mmallUser = new MmallUser();
        mmallUser.setUsername(loginRequest.getUsername());
        mmallUser.setPassword(MD5Util.MD5EncodeUtf8(loginRequest.getPassworld()));
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUser);
        if(mmallUserBack==null){
            return ServerResponse.createByErrorMessage("用户名或者密码有误，请核实后重新登入！");
        }
        // 置空加密信息
        mmallUserBack.setPassword(null);
        mmallUserBack.setQuestion(null);
        mmallUserBack.setAnswer(null);
        // 保存登入用户信息，session
        session.setAttribute(Const.CURRENT_USER,mmallUserBack);
        return ServerResponse.createBySuccess("登入成功！",mmallUserBack);
    }

    /**
     * 新用户注册
     * @param registerRequest
     * @return
     */
    public ServerResponse registerUser(PreRegisterRequest registerRequest){
        MmallUser mmallUserInsert = new MmallUser();
        // 验证用户名
        ServerResponse serverResponse = this.checkUserParam(registerRequest.getUsername(),Const.USERNAME);
        if(serverResponse.isSuccess()){
            return serverResponse;
        }
        // 验证邮箱
        serverResponse = this.checkUserParam(registerRequest.getEmail(),Const.EMAIL);
        if(serverResponse.isSuccess()){
            return serverResponse;
        }
        // 保存用户信息
        final Date nowTime = new Date();
        mmallUserInsert.setUsername(registerRequest.getUsername());
        mmallUserInsert.setPassword(MD5Util.MD5EncodeUtf8(registerRequest.getPassword()));
        mmallUserInsert.setEmail(registerRequest.getEmail());
        mmallUserInsert.setQuestion(registerRequest.getQuestion().trim());
        mmallUserInsert.setAnswer(registerRequest.getAnswer().trim());
        mmallUserInsert.setPhone(registerRequest.getPhone());
        mmallUserInsert.setCreateTime(nowTime);
        mmallUserInsert.setUpdateTime(nowTime);
        if(mmallUserMapper.insert(mmallUserInsert)==0){
            return ServerResponse.createByErrorMessage("用户注册失败！");
        }
        return ServerResponse.createBySuccessMessage("用户注册成功！");
    }

    /**
     * 检查用户参数是否可用
     * @param str 用户名 或 email
     * @param type username、email
     * @return
     */
    public ServerResponse checkUserParam(String str,String type){
        MmallUser mmallUserSelect = new MmallUser();
        String msg = "";
        if(Const.USERNAME.equals(type)){
            mmallUserSelect.setUsername(str);
            msg = "用户名重复！";
        }else if(Const.EMAIL.equals(type)){
            mmallUserSelect.setEmail(str);
            msg = "邮箱重复！";
        }
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUserSelect);
        if(mmallUserBack!=null){
            return ServerResponse.createByErrorMessage(msg);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取用户问题
     * @param username
     * @return
     */
    public ServerResponse question(String username){
        MmallUser mmallUserSelect = new MmallUser();
        mmallUserSelect.setUsername(username);
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUserSelect);
        if(mmallUserBack==null || mmallUserBack.getQuestion()==null){
            return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
        }
        return ServerResponse.createBySuccess(mmallUserBack.getQuestion());
    }

    /**
     * 验证用户密码答案是否正确，返回token值
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return
     */
    public ServerResponse question(String username,String question,String answer){
        MmallUser mmallUserSelect = new MmallUser();
        mmallUserSelect.setUsername(username);
        mmallUserSelect.setQuestion(question.trim());
        mmallUserSelect.setAnswer(answer.trim());
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUserSelect);
        if(null == mmallUserBack){
            return ServerResponse.createByErrorMessage("问题答案错误!");
        }
        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }


    /**
     * 未登入重置密码
     * @param username 用户名
     * @param passwordNew 新密码
     * @param forgetToken token值
     * @return
     */
    public ServerResponse resetPassWord(String username,String passwordNew,String forgetToken){
        MmallUser mmallUserSelect = new MmallUser();
        MmallUser mmallUserInsert = new MmallUser();
        mmallUserSelect.setUsername(username);
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUserSelect);
        if(null == mmallUserBack){
            return ServerResponse.createByErrorMessage("用户不存在!");
        }
        String forgetTokenIn =TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isEmpty(forgetTokenIn)){
            ServerResponse.createByErrorMessage("token过期，重新回答问题");
        }
        if(!StringUtils.equals(forgetToken,forgetTokenIn)){
            ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        mmallUserInsert.setId(mmallUserBack.getId());
        mmallUserInsert.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        if(mmallUserMapper.updateByPrimaryKeySelective(mmallUserInsert)==0){
            ServerResponse.createByErrorMessage("重置密码失败");
        }
        return ServerResponse.createBySuccess("修改密码成功！");
    }

    /**
     * 登入后更新用户信息
     * @param updateUserInfoRequest
     * @return
     */
    public ServerResponse updateUser(PreUpdateUserInfoRequest updateUserInfoRequest, MmallUser mmallUser){
        // 更新用户信息
        MmallUser mmallUserInsert = new MmallUser();
        mmallUserInsert.setId(mmallUser.getId());
        mmallUserInsert.setEmail(updateUserInfoRequest.getEmail());
        mmallUserInsert.setQuestion(updateUserInfoRequest.getQuestion().trim());
        mmallUserInsert.setAnswer(updateUserInfoRequest.getAnswer().trim());
        mmallUserInsert.setPhone(updateUserInfoRequest.getPhone());
        mmallUserInsert.setUpdateTime(new Date());
        if(mmallUserMapper.updateByPrimaryKeySelective(mmallUserInsert)==0){
            ServerResponse.createByErrorMessage("更新用户信息失败");
        }
        return ServerResponse.createBySuccess("更新成功！");
    }


}
