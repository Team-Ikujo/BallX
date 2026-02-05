package com.ballx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ballx.config.jwt.JwtAccessDeniedHandler;
import com.ballx.config.jwt.JwtAuthenticationEntryPoint;
import com.ballx.config.jwt.JwtAuthenticationFilter;
import com.ballx.config.oauth.CustomOAuth2UserService;
import com.ballx.config.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ballx.config.oauth.OAuth2AuthenticationFailureHandler;
import com.ballx.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.ballx.security.SecurityPath;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint entryPoint;
	private final JwtAccessDeniedHandler accessDeniedHandler;
	//OAuth2
	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			).authorizeHttpRequests(
				auth -> auth
					.requestMatchers(SecurityPath.PUBLIC).permitAll()
					.requestMatchers("/oauth2/redirect").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
				)
				.redirectionEndpoint(redirection -> redirection
					.baseUri("/api/*/auth/redirect")
				)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler)
			)
			.exceptionHandling(
				exceptions -> exceptions
					.authenticationEntryPoint(entryPoint)
					.accessDeniedHandler(accessDeniedHandler)
			).addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

}
