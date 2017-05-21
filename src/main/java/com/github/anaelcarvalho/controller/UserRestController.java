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
package com.github.anaelcarvalho.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.anaelcarvalho.model.User;
import com.github.anaelcarvalho.service.UserService;

/**
 * Defines service endpoints
 *
 * @author anaelcarvalho
 */
@RestController
@RequestMapping(value="/user")
public class UserRestController {
	@Autowired
	private UserService userService;

	/**
	 * Get User by user id
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable("userId") Integer userId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAllowed = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
		if(isAllowed) {
			User user = userService.getUserById(userId);
			if(user != null) {
				return ResponseEntity.ok(user);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * Create new user
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<User> addUser(@RequestBody User user) {
		if(user.getUserLogin() == null || user.getUserLogin().getLogin() == null) {
			return ResponseEntity.badRequest().build();
		}
		if(userService.addUser(user)) {
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
			return ResponseEntity.created(location).build();
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	/**
	 * Update user
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAllowed = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
		if(isAllowed) {
			userService.updateUser(user);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * Delete user
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable("userId") Integer userId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAllowed = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
		if(isAllowed) {
			userService.deleteUser(userId);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
}
