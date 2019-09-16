package com.mutil.userful.controller;

import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.service.PreUserService;
import com.mutil.userful.util.FastDFSClientUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = "/manage/user")
@Api(tags = "user", description = "（0.0.1 初始版本）")
public class LoginController {
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private FastDFSClientUtil dfsClient;
    @Autowired
    private PreUserService preUserService;

    /*@RequestMapping(value = "/upload",headers="content-type=multipart/form-data", method = RequestMethod.POST)
    public String uploadFile (@RequestParam("file") MultipartFile file,HttpServletRequest request){
        try {
            String fileUrl = dfsClient.uploadFile(file);
            request.setAttribute("msg", "成功上传文件，  '" + fileUrl + "'");
            log.info(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "login";
    }*/

    /**
     * 登录验证
     * @param account
     * @param password
     * @return
     */
    @RequestMapping("/login.do")
    @ResponseBody
    public ServerResponse<MmallUser> logion(String account, String password, HttpSession session){
        MmallUser user = preUserService.validateUser(account,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("登入失败，请核对账号密码！");
        }
        session.setAttribute(Const.CURRENT_USER,user);
        return ServerResponse.createBySuccess("登入成功！",user);
    }





}
