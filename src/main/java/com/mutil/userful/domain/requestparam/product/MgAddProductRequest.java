package com.mutil.userful.domain.requestparam.product;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MgAddProductRequest {

    @DecimalMin("0")
    @ApiModelProperty(value = "分类ID", required = true)
    private Integer categoryId;

    @NotNull
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @NotNull
    @ApiModelProperty(value = "副标题", required = true)
    private String subtitle;

    @NotNull
    @ApiModelProperty(value = "主图", required = true)
    private String mainImage;

    @NotNull
    @ApiModelProperty(value = "附图", required = true)
    private String subImages;

    @NotNull
    @ApiModelProperty(value = "详情", required = true)
    private String detail;

    @Min(0)
    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @DecimalMin("0")
    @ApiModelProperty(value = "数量", required = true)
    private Integer stock;

    @DecimalMin("0")
    @ApiModelProperty(value = "状态，上架、下架", required = true)
    private Integer status;

    @ApiModelProperty(value = "产品ID", required = true)
    private Integer id;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
