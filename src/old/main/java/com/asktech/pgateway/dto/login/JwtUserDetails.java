package com.asktech.pgateway.dto.login;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String token;
	private String pass;
	private String ipaddress;
	private Collection<? extends GrantedAuthority> authority;

	
	public JwtUserDetails(String userName, String token, String pass,String ipaddress,  Collection<? extends GrantedAuthority> authority) {
		super();
		this.userName = userName;
		this.token = token;
		this.pass = pass;
		this.ipaddress=ipaddress;
		this.authority = authority;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authority;
	}

	@Override
	public String getPassword() {
		
		return pass;
	}

	@Override
	public String getUsername() {
		
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getPass() {
		return pass;
	}
}
