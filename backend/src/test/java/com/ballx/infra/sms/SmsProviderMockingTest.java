package com.ballx.infra.sms;

import com.ballx.config.properties.sms.SmsProperties;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.nurigo.sdk.message.service.DefaultMessageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsProviderMockingTest {

	@Mock
	private SmsProperties smsProperties;

	@Mock
	private DefaultMessageService messageService;

	@InjectMocks
	private SmsProvider smsProvider;

	@Test
	@DisplayName("문자 전송 요청이 올바른 내용으로 CoolSMS SDK에 전달되는지 확인")
	void 문자_전송_mock_테스트() {
		String to = "01012345678";
		String code = "123456";
		String sender = "021234567";
		String contentTemplate = "인증번호는 [{code}] 입니다.";
		String expectedContent = "인증번호는 [123456] 입니다.";

		given(smsProperties.sender()).willReturn(sender);
		given(smsProperties.content()).willReturn(contentTemplate);

		SingleMessageSentResponse mockResponse = mock(SingleMessageSentResponse.class);
		given(messageService.sendOne(any())).willReturn(mockResponse);

		smsProvider.send(to, code);

		verify(messageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));

		ArgumentCaptor<SingleMessageSendingRequest> captor = ArgumentCaptor.forClass(SingleMessageSendingRequest.class);
		verify(messageService).sendOne(captor.capture());

		Message capturedMessage = captor.getValue().getMessage();
		assertThat(capturedMessage.getTo()).isEqualTo(to);
		assertThat(capturedMessage.getFrom()).isEqualTo(sender);
		assertThat(capturedMessage.getText()).isEqualTo(expectedContent);
	}
}
