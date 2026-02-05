package com.ballx.config.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

import com.ballx.config.properties.RestClientProperties;
import com.ballx.exception.client.ExternalClientException;
import com.ballx.exception.client.ExternalServerException;
import com.github.tomakehurst.wiremock.WireMockServer;

@SpringJUnitConfig
@Import(RestClientConfig.class)
@EnableConfigurationProperties(RestClientProperties.class)
@TestPropertySource(properties = {
	"rest-client.connect-timeout-ms=1000",
	"rest-client.read-timeout-ms=1000"
})
public class RestClientConfigTest {
	@Autowired
	@Qualifier("externalRestClient")
	RestClient externalRestClient;

	static WireMockServer wm;

	@BeforeAll
	static void startWireMock() {
		wm = new WireMockServer(0);
		wm.start();
		configureFor("localhost", wm.port());
	}

	@AfterAll
	static void stopWireMock() {
		wm.stop();
	}

	@BeforeEach
	void reset() {
		wm.resetAll();
	}

	@Test
	void 외부_API_4xx_응답_시_ExternalClientException_발생한다() {
		wm.stubFor(get(urlEqualTo("/test/4xx"))
			.willReturn(aResponse().withStatus(400).withBody("{\"error\":\"bad\"}")));

		String url = "http://localhost:" + wm.port() + "/test/4xx";

		assertThatThrownBy(() ->
			externalRestClient.get().uri(url).retrieve().body(String.class)
		).isInstanceOf(ExternalClientException.class);
	}

	@Test
	void 외부_API_5xx_응답_시_ExternalServerException_발생한다() {
		wm.stubFor(get(urlEqualTo("/test/5xx"))
			.willReturn(aResponse().withStatus(500).withBody("{\"error\":\"down\"}")));

		String url = "http://localhost:" + wm.port() + "/test/5xx";

		assertThatThrownBy(() ->
			externalRestClient.get().uri(url).retrieve().body(String.class)
		).isInstanceOf(ExternalServerException.class);
	}

	@Test
	void 외부_API_요청_시_X_Request_Id_헤더가_자동으로_추가된다() {
		wm.stubFor(get(urlEqualTo("/test/trace"))
			.willReturn(aResponse().withStatus(200).withBody("ok")));

		String url = "http://localhost:" + wm.port() + "/test/trace";

		String body = externalRestClient.get().uri(url).retrieve().body(String.class);
		assertThat(body).isEqualTo("ok");

		wm.verify(getRequestedFor(urlEqualTo("/test/trace"))
			.withHeader("X-Request-Id", matching(".+")));
	}
}
