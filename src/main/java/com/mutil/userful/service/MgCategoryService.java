package com.mutil.userful.service;

import com.google.common.collect.Lists;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.MmallCategoryMapper;
import com.mutil.userful.domain.MmallCategory;
import com.mutil.userful.domain.MmallUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhu.liang.common.requestparam.category.MgAddCategoryRequest;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class MgCategoryService {

    private static final Logger log = LoggerFactory.getLogger(MgCategoryService.class);

    @Autowired
    private MmallCategoryMapper mmallCategoryMapper;

    /**
     * 检查是否为管理员
     * @param mmallUser
     * @return
     */
    public ServerResponse checkAdmin(MmallUser mmallUser){
        if(mmallUser.getRole().intValue()== Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    /**
     * 查询子分类信息
     * @param categoryId
     * @return
     */
    public List<MmallCategory> getCategory(Integer categoryId){
        MmallCategory selectCategory = new MmallCategory();
        selectCategory.setParentId(categoryId);
        return mmallCategoryMapper.selectByMmallCategory(selectCategory);
    }

    /**
     * 添加分类信息
     * @param addCategoryRequest
     * @return
     */
    public ServerResponse addCategory(MgAddCategoryRequest addCategoryRequest){
        MmallCategory insertCategory = new MmallCategory();
        final Date nowTime = new Date();
        insertCategory.setParentId(addCategoryRequest.getParentId());
        insertCategory.setName(addCategoryRequest.getCategoryName());
        insertCategory.setStatus(true);
        insertCategory.setCreateTime(nowTime);
        insertCategory.setUpdateTime(nowTime);
        if(mmallCategoryMapper.insertSelective(insertCategory)==0){
            return ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 修改分类信息名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse changeCategory(Integer categoryId,String categoryName){
        MmallCategory updateCategory = new MmallCategory();
        updateCategory.setName(categoryName);
        updateCategory.setId(categoryId);
        if(mmallCategoryMapper.updateByPrimaryKeySelective(updateCategory)==0){
            return ServerResponse.createByErrorMessage("更新产品名字失败！");
        }
        return ServerResponse.createBySuccessMessage("更新产品名字成功！");
    }

    /**
     * 查询类别ID下所有子ID的值
     * @param categoryId
     * @return
     */
    public ServerResponse getAllCategoryIds(Integer categoryId){
        HashSet<Integer> set = new HashSet<>();
        List<Integer> listIds = Lists.newArrayList();
        try{
            set.add(categoryId);
            this.getAllDeepCategory(set,categoryId);
            for(Integer id:set){
                listIds.add(id);
            }
            return ServerResponse.createBySuccess(listIds);
        }catch (Exception e){
            log.info("查找分类信息失败！错误信息：{}",e.getMessage());
        }
        return ServerResponse.createByError();
    }

    private void getAllDeepCategory(HashSet<Integer> set,Integer categoryId){
        List<Integer> list = mmallCategoryMapper.selectCategoryIdByParentId(categoryId);
        list.stream().forEach(id->{
            set.add(id);
            this.getAllDeepCategory(set,id);
        });
    }



}
