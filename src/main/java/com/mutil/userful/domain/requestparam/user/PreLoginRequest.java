package com.mutil.userful.domain.requestparam.user;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class PreLoginRequest {

    @NotNull(message="用户名不能为空")
    @ApiModelProperty(value = "登录用户名", required = true)
    private String username;

    @NotNull(message="密码不能为空")
    @ApiModelProperty(value = "登录密码", required = true)
    private String passworld;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassworld() {
        return passworld;
    }

    public void setPassworld(String passworld) {
        this.passworld = passworld;
    }
}
