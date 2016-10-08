package com.digitalchina.modules.pay.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.digitalchina.utils.ChinapayUtil;
import com.digitalchina.utils.HttpUtil;
import com.digitalchina.utils.SignUtil;
import com.digitalchina.utils.StringUtil;

/**
 *后续类交易主要是指对已发生的交易，做后续的相关的交易处理；
 *包括：退款、退款撤销、退款重汇、消费撤销、预授权撤销、预授权完成撤销、
 *预授权完成、通知分账。
 *退款的相关操作也可以在企业门户系统中进行操作，
 *但是商户只能选择一种方式进行操作。
 *
 * @author shenjyb
 */
@Controller
public class SubseqTransactionController {

	
	private static final Log log = LogFactory.getLog(OrderComfirmController.class);
	
	/**后续交易提交数据地址
	 * 0401退款、0402退款撤销、0409退款重汇、9908通知分账的交易
	 */
	private static final String subUrl = "http://newpayment-test.chinapay.com/CTITS/service/rest/forward/syn/000000000065/0/0/0/0/0";
	
	private static final String VERIFY_KEY = "VERIFY_KEY";
	/**
	 * 
	 * 0401退款、0402退款撤销、0409退款重汇、9908通知分账的交易
	 * 
	 * 退款时，商户向chinapay发起退款，chinapay即时返回给商户 退款接收成功 的应答
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tuikuanreturn" ,method = RequestMethod.POST)
	public String subTran(Model model, HttpServletRequest request) {
		
		return "tuikuan";
	}
	
	/**
	 * 退款
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/tuikuai" ,method = RequestMethod.GET)
	public String postData(Model model, HttpServletRequest request,HttpServletResponse response) throws IOException {
		//
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("Version", "20140728");
		map.put("MerId", ChinapayUtil.MerId);
		//退款订单号
		map.put("MerOrderNo", ChinapayUtil.generateOrderNo());
		map.put("TranDate", ChinapayUtil.getCurrentTime()[0]);
		map.put("TranTime", ChinapayUtil.getCurrentTime()[1]);
		//原始交易订单号
		map.put("OriOrderNo", request.getParameter("oldno"));
		//原始商户交易日期
		map.put("OriTranDate", request.getParameter("olddate"));
		//退款金额
		map.put("RefundAmt", "1");
		//订单金额
		map.put("OrderAmt", "1");
		//交易类型
		/*
		* 0403消费撤销  0401退款交易  0202预授权完成  0203预授权撤销  
		* 0204预授权完成撤销  0402 退款撤销  0409 退款重汇  9908 通知分账
		*/
		map.put("TranType", "0403");
		//业务类型  固定值
		map.put("BusiType", "0001");
		//商户后台通知地址
		map.put("MerBgUrl", "house");
		
		//参数签名
		SecssUtil secssUtil = new SecssUtil();
		secssUtil.init();
		secssUtil.sign(map);
		//商户报文签名信息，报文中的所有字段都参与签名（Signature除外）
		String signature = secssUtil.getSign();
		if(!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())){
			log.info("签名过程发生错误，错误信息为-->" + secssUtil.getErrMsg());
			System.out.println("签名过程发生错误，错误信息为-->"+secssUtil.getErrMsg());
			return null;
		}
		map.put("Signature", signature);
		List<NameValuePair> formparams = HttpUtil.getNameValue(map);
		
		CloseableHttpResponse httpResonse = HttpUtil.sendToOtherServer(subUrl, map);
		if(null==httpResonse){
			//
			response.getWriter().write("发送失败");
		}else{
		  String respStr = StringUtil.parseResponseToStr(httpResonse);
		  System.out.println(respStr);
		  if(!respStr.contains("=")){
			 response.getWriter().write(respStr);
			 return null;
		  }
		  Map<String, String> resultMap = StringUtil.paserStrtoMap(respStr);
		  
		  //
		  for(Map.Entry<String, String> entry:resultMap.entrySet()){
			  request.setAttribute(entry.getKey(), entry.getValue());
		  }
		//验证签名
		String respCode = resultMap.remove("respCode");
		String respMsg = resultMap.remove("respMsg");
		if("0000".equals(respCode)){
			if(SignUtil.verify(resultMap)){
				resultMap.put(VERIFY_KEY, "success");
				request.setAttribute(VERIFY_KEY, "success");
			}else{
				resultMap.put(VERIFY_KEY, "fail");
				request.setAttribute(VERIFY_KEY, "fail");
			}
		}
		resultMap.put("respMsg", respMsg);
		resultMap.put("respCode", respCode);
		}
		//String html = ChinapayUtil.createAutoFormHtml(subUrl, map, "UTF-8");
		if(request.getParameter("oldno")==null){
			request.setAttribute("params", "oldno  olddate");
		}
	    return "/test/tuikuan"; 
	}
	
	
}
