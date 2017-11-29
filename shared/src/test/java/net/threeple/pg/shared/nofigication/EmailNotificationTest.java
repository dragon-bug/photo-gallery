package net.threeple.pg.shared.nofigication;

import org.junit.Test;

import net.threeple.pg.shared.notification.EmailNotification;

public class EmailNotificationTest {
	
	@Test
	public void testSendEmail() {
		EmailNotification en = new EmailNotification();
		en.send("这是一封测试邮件", "已经有10分钟未收到存储节点#8的心跳包，请尽快处理。");
	}
}
