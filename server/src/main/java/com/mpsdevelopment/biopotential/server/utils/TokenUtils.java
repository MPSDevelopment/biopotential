package com.mpsdevelopment.biopotential.server.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.firebase.security.token.TokenGenerator;
import com.firebase.security.token.TokenOptions;
import com.mpsdevelopment.biopotential.server.db.pojo.Token;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.Role;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.TokenType;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

	private static final Logger LOGGER = LoggerUtil.getLogger(TokenUtils.class);

	private static String SECRET_CODE = "q235asdg4aeayffewf";

	public String createToken(Token.TokenType tokenType, String userId, Token.Role role, Date expirationDate, Date notBeforeDate) {
		Map<String, Object> authPayload = new HashMap<>();
		authPayload.put(Token.FIELD_ID, userId);
		authPayload.put(Token.FIELD_TYPE, tokenType);
		authPayload.put(Token.FIELD_ROLE, role);

		TokenOptions tokenOptions = new TokenOptions();
		tokenOptions.setAdmin(true);
		tokenOptions.setExpires(expirationDate);
		tokenOptions.setNotBefore(notBeforeDate);

		TokenGenerator tokenGenerator = new TokenGenerator(SECRET_CODE);
		return tokenGenerator.createToken(authPayload, tokenOptions);
	}

	public String getUidFromToken(String token) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		Map<String, Object> decodedPayload = new JWTVerifier(SECRET_CODE).verify(token);
		LOGGER.debug("Payload is %s", decodedPayload);
		Token accessToken = JsonUtils.fromJson(Token.class, decodedPayload.get("d").toString());
		return accessToken.getUid();
	}

	public TokenType getTypeFromToken(String token)
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		Map<String, Object> decodedPayload = new JWTVerifier(SECRET_CODE).verify(token);
		LOGGER.debug("Payload is %s", decodedPayload);
		Token accessToken = JsonUtils.fromJson(Token.class, decodedPayload.get("d").toString());
		return accessToken.getType();
	}

	public Role getRoleFromToken(String token) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		if (StringUtils.isBlank(token)) {
			LOGGER.debug("Payload isBlank = %s", token);
			return null;
		}
		Map<String, Object> decodedPayload = new JWTVerifier(SECRET_CODE).verify(token);
		LOGGER.debug("Payload is %s", decodedPayload);
		Token accessToken = JsonUtils.fromJson(Token.class, decodedPayload.get("d").toString());
		LOGGER.debug("accessToken is %s", accessToken);
		return accessToken.getRole();
	}

}
