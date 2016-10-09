package com.digitalchina.modules.pay.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.digitalchina.utils.ChinapayUtil;

/**
 * 消费类交易
 * @author shenjyb
 */
@Controller
public class OrderComfirmController {

	private static final Log log = LogFactory.getLog("RT");
	
	@RequestMapping(value = "/pay" ,method = RequestMethod.GET)
	public String topay(Model model, HttpServletRequest request) {
		
	     String url = "http://newpayment-test.chinapay.com/CTITS/service/rest/page/nref/000000000017/0/0/0/0/0";
	     Map<String, String> params = ChinapayUtil.getParamsMap(request);
	     //参数签名
		 SecssUtil secssUtil = new SecssUtil();
		 secssUtil.init();
		 secssUtil.sign(params);
		//商户报文签名信息，报文中的所有字段都参与签名（Signature除外）
		 String Signature = secssUtil.getSign();
		 if(!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())){
			log.info("签名过程发生错误，错误信息为-->" + secssUtil.getErrMsg());
			System.out.println("签名过程发生错误，错误信息为-->"+secssUtil.getErrMsg());
			return null;
		 }
		 log.info(" 签名成功， 银联订单 " + params.get("MerOrderNo") +" 开始支付！");
		 params.put("Signature", Signature);
	     
	     log.info("create pay form , params:" + params);
    	 String html = ChinapayUtil.createAutoFormHtml(url, params, "UTF-8");
	     model.addAttribute("body", html);
	     return "/test/pay"; 
	}
	
	/**
	 * chinapay 返回支付回执
	 * 支付交易完成后，支付应答会分前台页面跳转和后台Http通知方式返回给商户，
	 * 商户需要对ChinaPay返回报文签名进行验签，以确定此报文是由ChinaPay发出
	 */
	@RequestMapping(value="/chinapayrecv",method = RequestMethod.POST)
	public void payBackInfo(HttpServletRequest request){
		log.info("接收chinapay发出的后台通知开始");
		Map<String,String[]> paramsMap = request.getParameterMap();
		//验证签名。
		SecssUtil secssUtil = new SecssUtil();
		secssUtil.init();
		secssUtil.verify(paramsMap);
		if(!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())){
			//验签失败
			log.info("接收到chinapay发出后台通知的参数,验签失败  "+secssUtil.getErrMsg());
			return;
		}
		log.info("接收到chinapay发出后台通知的参数"+paramsMap);
		log.info("订单支付状态 ： "+paramsMap.get("OrderStatus"));
		
		if("0000".equals(paramsMap.get("OrderStatus"))){
			//支付成功，修改订单状态
			log.info(paramsMap.get("MerOrderNo")+"订单支付成功!");
		}else if("0001".equals(paramsMap.get("OrderStatus"))){
			//未支付
			log.info(paramsMap.get("MerOrderNo")+"订单未支付!");
		}else{
			log.info(paramsMap.get("MerOrderNo")+"订单支付失败!");
		}
		
	}
	
	
	

}
