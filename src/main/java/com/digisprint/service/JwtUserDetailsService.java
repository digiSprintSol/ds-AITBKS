package com.digisprint.service;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.digisprint.utils.ApplicationConstants;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (StringUtils.isNotEmpty(username)) {
			return new User(username, ApplicationConstants.NONE,
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException(ApplicationConstants.USER_NOT_FOUND_WITH_USERNAME + username);
		}
	}

}
