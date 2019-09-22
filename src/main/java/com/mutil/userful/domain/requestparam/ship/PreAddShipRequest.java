package com.mutil.userful.domain.requestparam.ship;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PreAddShipRequest {

    @ApiModelProperty(value = "地址ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @NotNull
    @ApiModelProperty(value = "收货姓名", required = true)
    private String receiverName;

    @Pattern(regexp = "/^[0-9]+.?[0-9]*$/",message = "固定电话格式不符合要求")
    @ApiModelProperty(value = "收货固定电话", required = true)
    private String receiverPhone;

    @Pattern(regexp = "^1(3|4|5|7|8|9)\\d{9}$",message = "手机格式不符合要求")
    @ApiModelProperty(value = "收货移动电话", required = true)
    private String receiverMobile;

    @NotNull
    @ApiModelProperty(value = "省份", required = true)
    private String receiverProvince;

    @NotNull
    @ApiModelProperty(value = "城市", required = true)
    private String receiverCity;

    @NotNull
    @ApiModelProperty(value = "详细地址", required = true)
    private String receiverAddress;

    @Pattern(regexp = "/^[0-8][0-7]\\d{4}$/",message = "邮编不符合要求")
    @ApiModelProperty(value = "邮编", required = true)
    private String receiverZip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }
}
