package com.digitalchina.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import com.chinapay.secss.SecssUtil;

public class ChinapayUtil {
	
	/**消费类交易接口*/
	public static final String chinapayInterface = "http://newpayment-test.chinapay.com/CTITS/service/rest/page/nref/000000000017/0/0/0/0/0";
	
	/**由ChinaPay分配的15位定长数字，用于确认商户身份*/
	public static final String MerId = "000001609120969";
	
	public static final String Version = "20140728";
	
	/**交易前台应答地址*/
	public static final String MerPageUrl = "www.baidu.com";//ResourceBundle.getBundle("application").getString("app.main.dn");
	
	/**交易后台应答地址*/
	public static final String MerBgUrl = ResourceBundle.getBundle("payconfig").getString("pay.bgUrl");
	
	public static Map<String,String> getParamsMap(){
		Map<String, String> map = new HashMap<String, String>();
		//固定值
		map.put("Version", Version);
		//map.put("AccessType", "20160926");
		//map.put("AcqCode", "20160926");
		//由ChinaPay分配的15位定长数字，用于确认商户身份
		map.put("MerId", MerId);
		//必填，变长 32位，同一商户同一交易日期内不可重复
		map.put("MerOrderNo", "29123456789012");
		//商户提交交易的日期
		map.put("TranDate", getCurrentTime()[0]);
		//商户提交交易的时间，例如交易时间10点11分22秒，则值为101122
		map.put("TranTime", getCurrentTime()[1]);
		//订单金额   单位：分
		map.put("OrderAmt", "1");
		//交易类型
		/*
		 *  0001个人网银支付 0002企业网银支付 0003授信交易 0004快捷支付
			0005账单支付 0006认证支付 0007分期付款 0201预授权交易
		 */
		map.put("TranType", "0001");
		//业务类型  固定值
		map.put("BusiType", "0001");
		//交易币种
		//map.put("CurryNo", "CNY");
		//分账类型 不分账不填写此域；如需要分账，填写格式如下：0001：实时分账 0002：延时分账
		//map.put("SplitType", "0001");
		//分账方式    0：按金额分账   1：按比例分账
		//map.put("SplitMethod", "0");
		//  页面接受应答地址，用于引导使用者支付后返回商户网站页面 
		map.put("MerPageUrl", MerPageUrl);
		//  商户后台交易应答接收地址，ChinaPay会根据后台商户响应来判定是否重新发送后台应答流水，以确保后台应答的接收
		map.put("MerBgUrl", "");
		return map;
	}
	
	public static void main(String[] args) {
		SecssUtil secssUtil = new SecssUtil();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "1");
		map.put("MerPageUrl", MerPageUrl);
		secssUtil.sign(map);
		String sig = secssUtil.getSign();
		System.out.println(sig+"  "+map);
		
	}
    // 商户发送交易时间 格式:YYYYMMDDhhmmss
    public static String[] getCurrentTime() {
    	String[] time = new String[2];
    	time[0] = new SimpleDateFormat("yyyyMMdd").format(new Date());
    	time[1] = new SimpleDateFormat("HHmmss").format(new Date());
        return time;
    }
    
	public static String createAutoFormHtml(String url, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + url
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("<input type=\"submit\" value=\"提交订单\">");
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		//sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}
}
