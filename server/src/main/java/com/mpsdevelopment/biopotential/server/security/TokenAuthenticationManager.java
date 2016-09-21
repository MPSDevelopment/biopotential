package com.mpsdevelopment.biopotential.server.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.utils.TokenUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

@Service
public class TokenAuthenticationManager implements AuthenticationManager {

	private static final Logger LOGGER = LoggerUtil.getLogger(TokenAuthenticationManager.class);

	@Autowired
	private TokenUtils tokenUtils;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		LOGGER.info(" Token Authentication MANAGER Start WORK");
		try {
			if (authentication instanceof TokenAuthentication) {
				TokenAuthentication readyTokenAuthentication = processAuthentication((TokenAuthentication) authentication);
				return readyTokenAuthentication;
			} else {
				authentication.setAuthenticated(false);
				return authentication;
			}
		} catch (Exception ex) {
			if (ex instanceof AuthenticationServiceException)
				try {
					throw ex;
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	private TokenAuthentication processAuthentication(TokenAuthentication authentication) throws AuthenticationException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		String token = authentication.getToken();
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		try {
			String grantedRole = "ROLE_".concat(tokenUtils.getRoleFromToken(token).name());
			authorities.add(new SimpleGrantedAuthority(grantedRole));
		} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException | JWTVerifyException e) {
			e.printStackTrace();
		}

		User newUser = new User(tokenUtils.getUidFromToken(token), "root", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

		TokenAuthentication fullTokenAuthentication = new TokenAuthentication(authentication.getToken(), authorities, true, newUser);

		return fullTokenAuthentication;
	}
}