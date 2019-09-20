package com.mutil.userful.domain.requestparam.user;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PreUpdateUserInfoRequest {

    @Pattern(regexp="\\w+@\\w+(\\.[a-zA-Z]+)+",message="email 格式不符合要求")
    @ApiModelProperty(value = "email", required = true)
    private String email;

    @Pattern(regexp = "^1(3|4|5|7|8|9)\\d{9}$",message = "手机格式不符合要求")
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @NotNull(message="问题不能为空")
    @ApiModelProperty(value = "问题", required = true)
    private String question;

    @NotNull(message="答案不能为空")
    @ApiModelProperty(value = "答案", required = true)
    private String answer;


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
