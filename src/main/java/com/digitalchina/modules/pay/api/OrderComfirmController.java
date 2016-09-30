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
	 * chinapay ����֧����ִ
	 * ֧��������ɺ�֧��Ӧ����ǰ̨ҳ����ת�ͺ�̨Http֪ͨ��ʽ���ظ��̻���
	 * �̻���Ҫ��ChinaPay���ر���ǩ��������ǩ����ȷ���˱�������ChinaPay����
	 */
	@RequestMapping(value="/panel/chinapayrecv",method = RequestMethod.POST)
	public void payBackInfo(HttpServletRequest request){
		log.info("����chinapay�����ĺ�̨֪ͨ��ʼ");
		String encoding = request.getParameter("encoding");
		Map<String,String[]> paramsMap = request.getParameterMap();
		log.info("���յ�chinapay������̨֪ͨ�Ĳ���"+paramsMap);
		
	}
	
	
	

}
