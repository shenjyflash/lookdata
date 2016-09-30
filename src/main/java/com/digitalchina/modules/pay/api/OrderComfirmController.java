package com.digitalchina.modules.pay.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.digitalchina.utils.ChinapayUtil;

@Controller
public class OrderComfirmController {

	private static final Log log = LogFactory.getLog(OrderComfirmController.class);
	
	@RequestMapping(value = "/panel/pay" ,method = RequestMethod.GET)
	public String topay(Model model, HttpServletRequest request) {
		
	     String url = "http://newpayment-test.chinapay.com/CTITS/service/rest/page/nref/000000000017/0/0/0/0/0";
	     Map<String, String> params = ChinapayUtil.getParamsMap();
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
	@RequestMapping(value="/panel/chinapayrecv",method = RequestMethod.POST)
	public void payBackInfo(HttpServletRequest request){
		log.info("接收chinapay发出的后台通知开始");
		String encoding = request.getParameter("encoding");
		Map<String,String[]> paramsMap = request.getParameterMap();
		log.info("接收到chinapay发出后台通知的参数"+paramsMap);
		
	}
	
	
	

}
