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
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.anaelcarvalho.model.User;
import com.github.anaelcarvalho.model.UserLogin;
import com.github.anaelcarvalho.service.SecurityService;
import com.github.anaelcarvalho.service.UserService;

/**
 * Defines user-facing endpoints
 *
 * @author anaelcarvalho
 */
@Controller
public class WebPageController {
	@Autowired
	private UserService userService;
	@Autowired
	private SecurityService securityService;

	/**
	 * Initial page (welcome.html)
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String welcome(Map<String, Object> model) {
		URI loginUrl = ServletUriComponentsBuilder.fromPath("/login").build().toUri();
		URI registrationUrl = ServletUriComponentsBuilder.fromPath("/registration").build().toUri();
		URI homeUrl = ServletUriComponentsBuilder.fromPath("/home").build().toUri();
		model.put("loginUrl", loginUrl);
		model.put("registrationUrl", registrationUrl);
		model.put("homeUrl", homeUrl);
		return "welcome";
	}

	/**
	 * Login page (login.html)
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/login")
	public String login(Map<String, Object> model, String error, String logout, HttpServletRequest request) {
		if (error != null) { 
			model.put("error", "Your username and password is invalid.");
			model.put("formGroup", "has-error");
		} else {
			model.put("error", "");
			model.put("formGroup", "");
		}
		URI loginUrl = ServletUriComponentsBuilder.fromPath("/login").build().toUri();
		URI registrationUrl = ServletUriComponentsBuilder.fromPath("/registration").build().toUri();
		model.put("loginUrl", loginUrl);
		model.put("registrationUrl", registrationUrl);
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.put("csrfTokenName", token.getParameterName()); 
		model.put("csrfToken", token.getToken()); 
		return "login";
	}

	/**
	 * Registration page (registration.html)
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/registration")
	public String registration(Map<String, Object> model, String error, String logout, HttpServletRequest request) {
		if (error != null) { 
			model.put("error", error);
			model.put("formGroup", "has-error");
		} else {
			model.put("error", "");
			model.put("formGroup", "");
		}
		URI loginUrl = ServletUriComponentsBuilder.fromPath("/login").build().toUri();
		URI registrationUrl = ServletUriComponentsBuilder.fromPath("/registration").build().toUri();
		model.put("loginUrl", loginUrl);
		model.put("registrationUrl", registrationUrl);
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.put("csrfTokenName", token.getParameterName()); 
		model.put("csrfToken", token.getToken()); 
		return "registration";
	}

	/**
	 * Registration processor, redirects to /home on success or /registration on failure
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/registration")
	public String register(
		@RequestParam(value = "firstName") @NotEmpty String firstName, 
		@RequestParam(value = "lastName") @NotEmpty String lastName, 
		@RequestParam(value = "email") @NotEmpty String email, 
		@RequestParam(value = "login") @NotEmpty String login, 
		@RequestParam(value = "password") @NotEmpty String password,
		HttpServletRequest request, RedirectAttributes redirectAttributes) {

		if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
				|| login.isEmpty() || password.isEmpty()) {
			redirectAttributes.addAttribute("error", "Please fill in all fields");
			return "redirect:/registration";
		}
		if(userService.userLoginExists(login)) {
			redirectAttributes.addAttribute("error", "Login already exists, please select another one");
			return "redirect:/registration";
		}
		if(userService.userExists(email)) {
			redirectAttributes.addAttribute("error", "Email already exists, please select another one");
			return "redirect:/registration";
		}
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		UserLogin userLogin = new UserLogin();
		userLogin.setUser(user);
		userLogin.setLogin(login);
		userLogin.setPassword(password);
		user.setUserLogin(userLogin);
		if(userService.addUser(user)) {
			securityService.autologin(user.getUserLogin().getLogin(), password);
			return "redirect:/home";
		} else {
			redirectAttributes.addAttribute("error", "Registration error, try again later...");
			return "redirect:/registration";
		}
	}

	/**
	 * Home page for logged in users (home.html)
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/home")
	public String home(Map<String, Object> model, HttpSession session, Principal principal) {
		URI logoutUrl = ServletUriComponentsBuilder.fromPath("/logout").build().toUri();
		model.put("logoutUrl", logoutUrl);
		return "home";
	}

	/**
	 * Provides logged in user data for xhr requests.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/userdata")
	public ResponseEntity<User> userdata(Map<String, Object> model, HttpSession session, Principal principal) {
		String login = principal.getName();
		UserLogin userLogin = userService.getUserLoginByLogin(login);
		User user = userService.getUserById(userLogin.getUserIdFK());
		if(user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
