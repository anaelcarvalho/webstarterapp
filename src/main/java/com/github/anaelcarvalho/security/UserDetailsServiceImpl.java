/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.anaelcarvalho.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.anaelcarvalho.model.UserLogin;
import com.github.anaelcarvalho.service.UserService;

/**
 * Implementation of UserDetailsService interface for authentication handling.
 *
 * @author anaelcarvalho
 * @see     org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		UserLogin userLogin = userService.getUserLoginByLogin(login);
		if(userLogin == null) { 
			throw new UsernameNotFoundException("User " + login + " not found");
		}
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(userLogin.getRole()));
		return new org.springframework.security.core.userdetails.User(userLogin.getLogin(), userLogin.getPassword(), grantedAuthorities);
	}
}
