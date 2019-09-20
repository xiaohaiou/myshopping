package com.mutil.userful.domain.requestparam.category;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class MgAddCategoryRequest {

    @NotNull(message="父类ID不能为空")
    @ApiModelProperty(value = "父类ID", required = true)
    private Integer parentId;

    @NotNull(message="类别名称不能为空")
    @ApiModelProperty(value = "类别名称", required = true)
    private String categoryName;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
