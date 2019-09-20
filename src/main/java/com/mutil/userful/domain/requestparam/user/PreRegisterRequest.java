package com.mutil.userful.domain.requestparam.user;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PreRegisterRequest {

    @NotNull(message="用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotNull(message="密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Pattern(regexp="\\w+@\\w+(\\.[a-zA-Z]+)+",message="email 格式不符合要求")
    @ApiModelProperty(value = "email", required = true)
    private String email;

    @Pattern(regexp = "^1(3|4|5|7|8|9)\\d{9}$",message = "手机格式不符合要求")
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @NotNull(message = "问题不能为空")
    @ApiModelProperty(value = "问题", required = true)
    private String question;

    @NotNull(message="答案不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String answer;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
