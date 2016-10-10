package com.digitalchina.modules.pay.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinapay.secss.SecssConstants;
import com.chinapay.secss.SecssUtil;
import com.digitalchina.utils.ChinapayUtil;
import com.digitalchina.utils.HttpUtil;
import com.digitalchina.utils.SignUtil;
import com.digitalchina.utils.StringUtil;

/**
 * 查询交易
 * @author shenjyb
 */
@Controller
public class QueryTranController {

	private static final Log log = LogFactory.getLog("RT");
	
	private static final String VERIFY_KEY = "VERIFY_KEY";
	
	/**
	 * 查询交易
	 * @param model
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/query" ,method = RequestMethod.GET)
	public String queryTrade(Model model, HttpServletRequest request, HttpServletResponse response){
		log.info("查询订单信息，订单号：-->" + request.getParameter("oldorderid"));
	    String url = "http://newpayment-test.chinapay.com/CTITS/service/rest/forward/syn/000000000060/0/0/0/0/0";
	    Map<String,String> map = new HashMap<String,String>();
		map.put("Version", "20140728");
		map.put("MerId", ChinapayUtil.MerId);
		//退款订单号
		map.put("MerOrderNo", request.getParameter("oldorderid"));
		map.put("TranDate", request.getParameter("olddate"));
		//业务类型  固定值
		map.put("BusiType", "0001");
		//交易类型
		map.put("TranType", "0502");
		
	    //参数签名
		SecssUtil secssUtil = new SecssUtil();
		secssUtil.init();
		secssUtil.sign(map);
		//商户报文签名信息，报文中的所有字段都参与签名（Signature除外）
		String Signature = secssUtil.getSign();
		if(!SecssConstants.SUCCESS.equals(secssUtil.getErrCode())){
			log.info("签名过程发生错误，错误信息为-->" + secssUtil.getErrMsg());
			System.out.println("签名过程发生错误，错误信息为-->"+secssUtil.getErrMsg());
			return null;
		}
		log.info(" 签名成功， 银联订单 " + map.get("MerOrderNo") +" 开始查询！");
		map.put("Signature", Signature);
	    log.info("create query form , params:" + map);
	    CloseableHttpResponse httpResonse = HttpUtil.sendToOtherServer(url, map);
	    if(null==httpResonse){
	    	log.info("查询失败！");
		}else{
			String respStr = null;
			try {
				respStr = StringUtil.parseResponseToStr(httpResonse);
			} catch (IOException e) {
				log.info("查询失败!------> IOException ");
				e.printStackTrace();
			}
			if(!respStr.contains("=")){
				log.info("查询失败！");
				return null;
			}
			Map<String, String> resultMap = StringUtil.paserStrtoMap(respStr);
			for(Map.Entry<String, String> entry:resultMap.entrySet()){
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			//验证签名
			SecssUtil secssUtil2 = new SecssUtil();
			secssUtil2.init();
			secssUtil2.verify(resultMap);
			
			if(secssUtil2.getErrCode().equals(SecssConstants.SUCCESS)){
				log.info("验签成功！");
			}
			String respCode = resultMap.get("respCode");
			if("0000".equals(respCode)){
				if(SignUtil.verify(resultMap)){
					resultMap.put(VERIFY_KEY, "success");
					request.setAttribute(VERIFY_KEY, "success");
					log.info("查询结果验签成功！");
				}else{
					resultMap.put(VERIFY_KEY, "fail");
					request.setAttribute(VERIFY_KEY, "fail");
					log.info("查询结果验签失败！");
				}
			}
		}
	    
	    if(request.getParameter("oldorderid")==null || request.getParameter("olddate")==null){
			request.setAttribute("params", "oldorderid  olddate");
		}
	    //展示交易查询结果
	    return "/test/tuikuan"; 
	}
	
}
