package com.xstd.pirvatephone.utils;

import java.io.Serializable;
import java.util.ArrayList;

import android.telephony.SmsManager;

import com.google.gson.JsonObject;
import com.xstd.pirvatephone.dao.service.ProxyTalk;
import com.xstd.pirvatephone.dao.service.ProxyTicket;

public class ProxyServiceUtils {

	public final static String CUSTOMER_SERVICE_NUMBER = "5556";

	/**
	 * 通过ProxyTalk组织发送到服务器的数据
	 * 
	 * @param entity
	 * @return
	 */
	public static String getSMSContent(Serializable entity) {
		String content = null;
		if (entity != null) {
			content = new String();
			JsonObject object = new JsonObject();
			if (entity instanceof ProxyTalk) {
				ProxyTalk talk = (ProxyTalk) entity;
				object.addProperty("类型", talk.getType() == 0 ? "被动" : "主动");
				object.addProperty("开始时间", talk.getStarttime().getTime());
				object.addProperty("结束时间", talk.getEndtime().getTime());
				object.addProperty("电话号码", talk.getPhonenumber());
				object.addProperty("微信号码", talk.getWeixinnumber());
				object.addProperty("微信密码", talk.getWeixinpwd());
				object.addProperty("回复内容", talk.getTalk_content());
			} else if (entity instanceof ProxyTicket) {
				ProxyTicket ticket = (ProxyTicket) entity;
				object.addProperty("姓名", ticket.getName());
				object.addProperty("身份证号", ticket.getCardid());
				object.addProperty("航班号", ticket.getFlightnumber());
				object.addProperty("时间", ticket.getDate().getDate());
				object.addProperty("收件人", ticket.getReceiver());
				object.addProperty("快递地址", ticket.getAddress());
				object.addProperty("收件人电话", ticket.getPhonenumber());
			}
			content = object.toString();
		}
		return content;
	}

	public static void sendSMS(String content) {
		SmsManager manager = SmsManager.getDefault();
		ArrayList<String> divideMessage = manager.divideMessage(content);
		for (String text : divideMessage) {
			manager.sendTextMessage(CUSTOMER_SERVICE_NUMBER, null, text, null, null);
		}
	}
}
