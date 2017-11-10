package com.hdkj.notify.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author xiey
 * @version 2017年11月9日 下午3:52:38
 * @describe
 */
@Component
public class Consumer {
	// 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
	@JmsListener(destination = "mytest.queue")
	// @SendTo("out.queue") 会将return的返回值发送到out.queue
	public void receiveQueue(String text) {
		System.out.println("Consumer收到的报文为:" + text);
//		return text;
	}
}