package com.ballx.service.auth;

import com.ballx.domain.dto.request.AuthRequest;
import com.ballx.service.auth.dto.TokenPair;

public interface AuthService {
	TokenPair login(String mobile, String deviceId);
}
