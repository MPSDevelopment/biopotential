package com.mpsdevelopment.biopotential.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mpsdevelopment.biopotential.server.db.advice.Adviceable;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	@Adviceable
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

		com.mpsdevelopment.biopotential.server.db.pojo.User domainUser = userDao.getByLogin(login);

		if (domainUser == null) {
			throw new UsernameNotFoundException("User not found");
		}

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		return new User(domainUser.getLogin(), domainUser.getPassword(), enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, getAuthorities(domainUser.getRole()));
	}

	public Collection<GrantedAuthority> getAuthorities(String role) {
		List<String> roles = getRoles();
		List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(roles, role);
		return grantedAuthorities;
	}

	public List<String> getRoles() {
		List<String> roles = new ArrayList<String>();
		for (Role role : Role.values()) {
			roles.add(role.name());
		}

		return roles;
	}

	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles, String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for (String authority : roles) {
			if (authority.equals(role)) {
				grantedAuthorities.add(new SimpleGrantedAuthority(authority));
			}
		}
		return grantedAuthorities;
	}
}
