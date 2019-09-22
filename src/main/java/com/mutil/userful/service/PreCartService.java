package com.mutil.userful.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mutil.userful.annotation.In;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.MmallCartMapper;
import com.mutil.userful.dao.MmallProductMapper;
import com.mutil.userful.domain.MmallCart;
import com.mutil.userful.domain.MmallProductWithBLOBs;
import com.mutil.userful.domain.vo.CartProductVo;
import com.mutil.userful.domain.vo.CartVo;
import com.mutil.userful.util.BigDecimalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class PreCartService {

    private static final Logger log = LoggerFactory.getLogger(PreCartService.class);

    @Autowired
    private MmallCartMapper cartMapper;
    @Autowired
    private MmallProductMapper productMapper;

    /**
     * 获取购物车列表信息
     * @param userId
     * @return
     */
    public ServerResponse cartList(Integer userId){
        CartVo cartVo = new CartVo();
        final List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPriceBg = new BigDecimal(0);

        List<MmallCart> cartList = cartMapper.selectCartsByUseId(userId);
        if(CollectionUtils.isNotEmpty(cartList)){
            for(MmallCart cart:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());
                MmallProductWithBLOBs product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(null!=product){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    // 判断库存
                    int buyLimitCount;
                    if(product.getStock()>=cart.getQuantity()){
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 更新购物车的限制数量
                        MmallCart updateMmllCart = new MmallCart();
                        updateMmllCart.setId(cart.getId());
                        updateMmllCart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(updateMmllCart);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    // 计算产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cart.getQuantity().doubleValue(),product.getPrice().doubleValue()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                // 已勾选，增加到购物车的总价中去
                if(cart.getChecked()==Const.Cart.CHECKED){
                    cartTotalPriceBg = BigDecimalUtil.add(cartTotalPriceBg.doubleValue(),
                                                          cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPriceBg);
        cartVo.setImageHost("TODO://");
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse add(Integer userId,Integer productId,Integer count){
        MmallCart selectMmallCart = new MmallCart();
        selectMmallCart.setUserId(userId);
        selectMmallCart.setProductId(productId);
        List<MmallCart> mmallCartList = cartMapper.selectCartByCart(selectMmallCart);
        if(CollectionUtils.isEmpty(mmallCartList)){
            MmallCart insertCart = new MmallCart();
            final Date nowTime = new Date();
            insertCart.setProductId(productId);
            insertCart.setUserId(userId);
            insertCart.setChecked(Const.Cart.CHECKED);
            insertCart.setQuantity(count);
            insertCart.setCreateTime(nowTime);
            insertCart.setUpdateTime(nowTime);
            cartMapper.insert(insertCart);
        }else{
            MmallCart updateMmallCart = mmallCartList.get(0);
            count+=updateMmallCart.getQuantity();
            updateMmallCart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(updateMmallCart);
        }
        return this.cartList(userId);
    }

    /**
     * 移除购物车中某个产品
     * @param userId
     * @param productIds
     * @return
     */
    public ServerResponse reomveProduct(Integer userId,String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorMessage("未找到对应产品信息！");
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.cartList(userId);
    }

    /**
     * 更新商品数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse update(Integer userId,Integer productId,Integer count){
        MmallCart selectMmallCart = new MmallCart();
        selectMmallCart.setUserId(userId);
        selectMmallCart.setProductId(productId);
        List<MmallCart> mmallCartList = cartMapper.selectCartByCart(selectMmallCart);
        if(!CollectionUtils.isEmpty(mmallCartList)){
            MmallCart updateMmallCart = mmallCartList.get(0);
            updateMmallCart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(updateMmallCart);
        }
        return this.cartList(userId);
    }

    /**
     * 购物车选中或者未选中某个商品
     * @param userId
     * @param productId
     * @return
     */
    public ServerResponse selectProduct(Integer userId,Integer productId){
        MmallCart selectMmallCart = new MmallCart();
        selectMmallCart.setUserId(userId);
        selectMmallCart.setProductId(productId);
        List<MmallCart> mmallCartList = cartMapper.selectCartByCart(selectMmallCart);
        if(CollectionUtils.isEmpty(mmallCartList)){
            return ServerResponse.createByErrorMessage("选择购物车商品失败，未找到对应商品！");
        }
        MmallCart updateMmallCart = mmallCartList.get(0);
        updateMmallCart.setChecked(updateMmallCart.getChecked()==1?0:1);
        if(cartMapper.updateByPrimaryKeySelective(updateMmallCart)==0){
            return ServerResponse.createByErrorMessage("选择购物车商品失败！");
        }
        return this.cartList(userId);
    }

    /**
     * 计算购物车产品数量
     * @param userId
     * @return
     */
    public ServerResponse getProductCount(Integer userId){
        Integer count = cartMapper.selectCartNum(userId);
        if(count==null){
            return ServerResponse.createByErrorMessage("计算购物车商品数量失败！");
        }
        return ServerResponse.createBySuccess(count);
    }

    /**
     * 购物车商品全选
     * @param userId
     * @return
     */
    public ServerResponse selectAll(Integer userId,Integer checked){
        if(cartMapper.updateForSelectOrUnSelectAll(userId,checked)==0){
            return ServerResponse.createByErrorMessage("购物车商品状态未改变！");
        }
        return this.cartList(userId);
    }

    /**
     * 判断购物车物品是否为全选状态
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
