package com.mutil.userful.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class LoginController {

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping("/")
    public String tologinPage() {
        log.info("进入首页");
        return "login";
    }

    /**
     * 登录验证
     * @param account
     * @param password
     * @return
     */
    @RequestMapping("/validateLogin")
    @ResponseBody
    public String logion(String account,String password,HttpServletRequest request){
        //验证是否能登录；生成session
        HttpSession session = request.getSession();
        String result = "fail";
//        SysUser user = userService.getUserByAccount(account);
//        List<String> menus = userService.getMenusByUserId(user.getId());
//        if(user != null && password.equals(user.getPassword())) {
//            result = "success";
//            session.setAttribute("userSession", user);
//            session.setAttribute("menuSession", menus);
//        }
        return result;
    }

    /**
     * 退出
     * @return
     */
    @RequestMapping("/exit")
    public String exit(HttpServletRequest request) {
        //清除session;跳转到登录页面
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
//        SysUser user = (SysUser) session.getAttribute("userSession");
        System.out.println("进入index方法");
        log.info("进入index方法");
        log.error("打印错误");
        return "index";
    }

}
