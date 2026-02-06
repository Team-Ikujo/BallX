package com.ballx.infra.sms;

import com.ballx.config.properties.sms.SmsProperties;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.NurigoApp;

@Slf4j
class SmsProviderTest {

	@Test
	@Disabled("실제 문자가 발송되며 비용이 발생합니다. 확인이 필요할 때만 @Disabled를 주석 후 수동으로 실행하세요.")
	void 실제_문자_전송_테스트() {

		SmsProperties smsProperties = new SmsProperties(
			"{SMS_API_KEY}", // api key 필요 시 데이터 주입 후 테스트 진행
			"{SMS_API_SECRET}", // api secret 필요 시 데이터 주입 후 테스트 진행
			"{SMS_API_SENDER}", // api sender 필요 시 데이터 주입 후 테스트 진행
			"인증번호는 [{code}] 입니다.",
			"https://api.coolsms.co.kr"
		);

		DefaultMessageService defaultMessageService = NurigoApp.INSTANCE.initialize(
			smsProperties.key(),
			smsProperties.secret(),
			smsProperties.domain()
		);

		SmsProvider smsProvider = new SmsProvider(smsProperties, defaultMessageService);

		String receiver = "{Receiver}"; // 필요 시 받는 이 휴대전화 번호 정확히 기입 후 테스트 진행

		smsProvider.send(receiver, "123123");

		log.info("실제 문자 발송 요청이 완료되었습니다.");
	}
}
