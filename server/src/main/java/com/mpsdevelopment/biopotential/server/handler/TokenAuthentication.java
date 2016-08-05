package com.mpsdevelopment.biopotential.server.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TokenAuthentication implements Authentication {
	
	private static final long serialVersionUID = -8128975069190073254L;
	
	private String token;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isAuthenticated;
    private UserDetails principal;
    private Object details;

    public TokenAuthentication(String token) {
        this.token = token;
//        this.details = details;
    }

    public TokenAuthentication(String token, Collection<SimpleGrantedAuthority> authorities, boolean isAuthenticated, 
                                                UserDetails principal) {
        this.token = token;
        this.authorities = authorities;
        this.isAuthenticated = isAuthenticated;
        this.principal = principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public String getName() {
        if (principal != null)
            return ((UserDetails) principal).getUsername();
        else
            return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        isAuthenticated = b;
    }

    public String getToken() {
        return token;
    }

}
