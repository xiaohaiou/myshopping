package com.mutil.userful.service;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mutil.userful.common.Const;
import com.mutil.userful.common.ServerResponse;
import com.mutil.userful.dao.*;
import com.mutil.userful.domain.*;
import com.mutil.userful.domain.vo.OrderItemVo;
import com.mutil.userful.domain.vo.OrderVo;
import com.mutil.userful.domain.vo.ShippingVo;
import com.mutil.userful.util.BigDecimalUtil;
import com.mutil.userful.util.DateTimeUtil;
import com.mutil.userful.util.FastDFSClientUtil;
import com.mutil.userful.util.PropertiesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class PreOrderService {

    private static final Logger log = LoggerFactory.getLogger(PreOrderService.class);

    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private MmallOrderMapper mmallOrderMapper;
    @Autowired
    private FastDFSClientUtil dfsClient;
    @Autowired
    private MmallOrderItemMapper mmallOrderItemMapper;
    @Autowired
    private MmallPayInfoMapper mmallPayInfoMapper;
    @Autowired
    private MmallCartMapper mmallCartMapper;
    @Autowired
    private MmallProductMapper mmallProductMapper;
    @Autowired
    private MmallShippingMapper mmallShippingMapper;

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse create(Integer userId,Integer shippingId){

        MmallCart selectMmallCart = new MmallCart();
        selectMmallCart.setUserId(userId);
        selectMmallCart.setChecked(1);
        List<MmallCart> mmallCartList = mmallCartMapper.selectCartByCart(selectMmallCart);
        if(CollectionUtils.isEmpty(mmallCartList)){
            return ServerResponse.createByErrorMessage("购物车所选商品为空！");
        }

        // 计算购物车商品总价
        ServerResponse orderItemResponse = this.getCartOrderItem(userId,mmallCartList);
        if(!orderItemResponse.isSuccess()){
            return orderItemResponse;
        }

        List<MmallOrderItem> mmallOrderItemList = (List<MmallOrderItem>)orderItemResponse.getData();
        BigDecimal paymentBD = this.getOrderTotalPrice(mmallOrderItemList);

        // 生成订单
        MmallOrder mmallOrder = this.createOrder(userId,shippingId,paymentBD);
        if(mmallOrder == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }

        if(CollectionUtils.isEmpty(mmallOrderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(MmallOrderItem orderItem : mmallOrderItemList){
            orderItem.setOrderNo(mmallOrder.getOrderNo());
        }
        //mybatis 批量插入
        mmallOrderItemMapper.insertBatch(mmallOrderItemList);
        // 扣减库存
        List<MmallProduct> mmallProductList = mmallProductMapper.selectByMmallCertList(mmallCartList);
        Map<Integer,MmallCart> cartMap = Maps.newHashMap();
        List<String> productIdList = Lists.newArrayList();
        mmallCartList.stream().forEach(cart->{
            cartMap.put(cart.getProductId(),cart);
        });
        mmallProductList.stream().forEach(product->{
            MmallCart cart = cartMap.get(product.getId());
            product.setStock(product.getStock()-cart.getQuantity());
            productIdList.add(product.getId().toString());
        });
        mmallProductMapper.updateByBatch(mmallProductList);
        // 清空购物车
        mmallCartMapper.deleteByUserIdProductIds(userId,productIdList);
        OrderVo orderItemVo = this.assembleOrderVo(mmallOrder,mmallOrderItemList);
        return ServerResponse.createBySuccess(orderItemVo);
    }

    // create -购物车产品信息->订单商品信息
    private ServerResponse getCartOrderItem(Integer userId,List<MmallCart> mmallCartList){

        // 获取产品信息
        List<MmallProduct> mmallProductList = mmallProductMapper.selectByMmallCertList(mmallCartList);
        if(CollectionUtils.isEmpty(mmallProductList)) return ServerResponse.createByErrorMessage("未匹配到对应的产品！");

        // 保存结果集容器
        Map<Integer,MmallProduct> mmallProductMap = Maps.newHashMap();
        List<MmallOrderItem> mmallOrderItemList = Lists.newArrayList();
        List<String> errorMessageList = new ArrayList();

        mmallProductList.stream().forEach(product->{
            mmallProductMap.put(product.getId(),product);
        });

        mmallCartList.stream().forEach(cartItem->{
            MmallProduct product = mmallProductMap.get(cartItem.getProductId());
            MmallOrderItem orderItem = new MmallOrderItem();
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                errorMessageList.add("产品"+product.getName()+"不是在线售卖状态");
            }
            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                errorMessageList.add("产品"+product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            mmallOrderItemList.add(orderItem);
        });
        if(!CollectionUtils.isEmpty(errorMessageList)){
            return ServerResponse.createByErrorMessage(errorMessageList.toString());
        }
        return ServerResponse.createBySuccess(mmallOrderItemList);
    }

    // create -获取购物车商品总价
    private BigDecimal getOrderTotalPrice(List<MmallOrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(MmallOrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    //  create -生成订单
    private MmallOrder createOrder(Integer userId,Integer shippingId,BigDecimal paymentBD){
        MmallOrder mmallOrder = new MmallOrder();
        mmallOrder.setOrderNo(this.generateOrderNo());
        mmallOrder.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        mmallOrder.setPostage(0);
        mmallOrder.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        mmallOrder.setPayment(paymentBD);

        mmallOrder.setUserId(userId);
        mmallOrder.setShippingId(shippingId);
        //发货时间等等
        //付款时间等等
        int rowCount = mmallOrderMapper.insert(mmallOrder);
        if(rowCount > 0){
            return mmallOrder;
        }
        return null;
    }

    // createOrder - 生成订单号
    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    // create -封装返回结果集
    private OrderVo assembleOrderVo(MmallOrder order, List<MmallOrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        MmallShipping shipping = mmallShippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            ShippingVo shippingVo = new ShippingVo();
            BeanUtils.copyProperties(shipping,shippingVo);
            orderVo.setShippingVo(shippingVo);
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(MmallOrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItem,orderItemVo);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    /**
     * 订单支付
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse pay(Integer userId,Long orderNo){

        MmallOrder selectMmallOrder = new MmallOrder();
        selectMmallOrder.setUserId(userId);
        selectMmallOrder.setOrderNo(orderNo);
        List<MmallOrder> orderList = mmallOrderMapper.selectOrderBySelectorBean(selectMmallOrder);

        if(CollectionUtils.isEmpty(orderList)){
            return ServerResponse.createByErrorMessage("用户未找到对应订单！");
        }
        Map<String,String> resultMap = Maps.newHashMap();
        MmallOrder mmallOrder = orderList.get(0);
        resultMap.put("orderNo",mmallOrder.getOrderNo().toString());

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = mmallOrder.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = "Mmall初始版当面付消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = mmallOrder.getPayment().toString();

        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append("订单")
                                         .append(mmallOrder.getOrderNo().toString())
                                         .append("购买商品的总价为")
                                         .append(mmallOrder.getPayment()).append("元")
                                         .toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        // 商品明细列表，需填写购买商品详细信息，
        MmallOrderItem selectMmallOrderItem = new MmallOrderItem();
        selectMmallOrderItem.setUserId(userId);
        selectMmallOrderItem.setOrderNo(orderNo);
        List<MmallOrderItem> mmallOrderItemList = mmallOrderItemMapper.selectByMmallOrderItem(selectMmallOrderItem);

        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        mmallOrderItemList.stream().forEach(orderItem->{
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getId().toString(),
                                                         orderItem.getProductName(),
                                                         orderItem.getTotalPrice().longValue(),
                                                         orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        });

        //String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))
                .setGoodsDetailList(goodsDetailList);

        // 调用tradePay方法获取当面付应答
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝支付成功!");
                AlipayTradePrecreateResponse response = result.getResponse();

                File folder = new File(Const.PICFILEPATH);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 项目存放临时文件，用于上传 FSTDFS 使用
                File targetFile = null;
                try {
                    // 需要修改为运行机器上的路径
                    // 细节细节细节
                    String qrPath = String.format(Const.PICFILEPATH+"/qr-%s.png",response.getOutTradeNo());
                    String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                    ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                    targetFile = new File(Const.PICFILEPATH,qrFileName);
                    // 上传文件
                    String qrUrl = dfsClient.uploadFile(targetFile);
                    log.info("qrPath:" + qrPath);
                    resultMap.put("qrUrl",qrUrl);
                    return ServerResponse.createBySuccess(resultMap);
                } catch (IOException e) {
                    log.error("上传二维码异常",e);
                }finally {
                    if(targetFile!=null && targetFile.exists()){
                        targetFile.deleteOnExit();
                    }
                }
            case FAILED:
                log.error("支付宝支付失败!!!");
                return ServerResponse.createByErrorMessage("支付宝支付失败!!!");
            case UNKNOWN:
                log.error("系统异常，订单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，订单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 支付宝支付回调函数
     * @param params
     * @return
     */
    public ServerResponse aliCallback(Map<String,String> params){
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        MmallOrder selectMmallOrder = new MmallOrder();
        selectMmallOrder.setOrderNo(orderNo);
        List<MmallOrder> mmallOrderList = mmallOrderMapper.selectOrderBySelectorBean(selectMmallOrder);
        if(CollectionUtils.isEmpty(mmallOrderList)){
            return ServerResponse.createByErrorMessage("非快乐慕商城的订单,回调忽略");
        }
        MmallOrder order = mmallOrderList.get(0);

        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }

        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            mmallOrderMapper.updateByPrimaryKeySelective(order);
        }
        MmallPayInfo payInfo = new MmallPayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        mmallPayInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    /**
     * 查询订单状态
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse orderStatus(Integer userId,Long orderNo){
        MmallOrder selectMmallOrder = new MmallOrder();
        selectMmallOrder.setUserId(userId);
        selectMmallOrder.setOrderNo(orderNo);
        List<MmallOrder> orderList = mmallOrderMapper.selectOrderBySelectorBean(selectMmallOrder);
        if(CollectionUtils.isEmpty(orderList)){
            return ServerResponse.createByErrorMessage("用户未找到对应订单！");
        }
        MmallOrder order = orderList.get(0);
        return ServerResponse.createBySuccess(order.getStatus());
    }

}
