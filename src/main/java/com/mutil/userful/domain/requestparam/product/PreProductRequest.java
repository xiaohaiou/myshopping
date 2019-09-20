package com.mutil.userful.domain.requestparam.product;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class PreProductRequest {

    @ApiModelProperty(value = "分类ID", required = true)
    private Integer categoryId;

    @ApiModelProperty(value = "关键字", required = true)
    private String keyword;

    @NotNull(message="页数不能为空")
    @ApiModelProperty(value = "第几页", required = true)
    private Integer pageNum;

    @NotNull(message="每页显示数据不能为空")
    @ApiModelProperty(value = "每页显示数据", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "排序方式,列如:price_desc", required = true)
    private String orderBy;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
