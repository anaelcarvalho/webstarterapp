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
package com.github.anaelcarvalho.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.anaelcarvalho.dao.UserDAO;
import com.github.anaelcarvalho.dao.UserLoginDAO;
import com.github.anaelcarvalho.model.User;
import com.github.anaelcarvalho.model.UserLogin;
import com.github.anaelcarvalho.service.UserService;

/**
 * Implementation of UserService interface for operations related to users.
 *
 * @author anaelcarvalho
 * @see     com.github.anaelcarvalho.service.UserService
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	//DB DAOs
	@Autowired
	@Qualifier("user-db")
	private UserDAO userDbDAO;
	@Autowired
	@Qualifier("login-db")
	private UserLoginDAO userLoginDbDAO;

	//Redis DAOS
	@Autowired
	@Qualifier("user-redis")
	private UserDAO userRedisDAO;
	@Autowired
	@Qualifier("login-redis")
	private UserLoginDAO userLoginRedisDAO;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User getUserById(int userId) {
		User user = null;
		try {
			user = userRedisDAO.getUserById(userId); //attempt to get from Redis first
			if(user == null) {
				user = userDbDAO.getUserById(userId); //if not found, try db
				if(user != null) {
					userRedisDAO.addUser(user); //if found in db, add to Redis
				}
			}
		} catch(Exception e) { //fallback to db
			logger.error("Error feching user from Redis, fallback to db...", e);
			user = userDbDAO.getUserById(userId);
		}
		return user;
	}

	@Override
	public boolean addUser(User user) {
		user.getUserLogin().setPassword(bCryptPasswordEncoder.encode(user.getUserLogin().getPassword())); //hash user pwd
		if(userLoginDbDAO.userLoginExists(user.getUserLogin().getLogin())|| userDbDAO.userExists(user.getEmail())) { //check Redis first
			return false;
		} else {
			Integer id = userDbDAO.addUser(user);
			user.getUserLogin().setUser(user);
			user.getUserLogin().setUserIdFK(id);
			userLoginDbDAO.addUserLogin(user.getUserLogin());
			try { //try adding user to Redis, do not let it raise exception on failure
				userRedisDAO.addUser(user);
				userLoginRedisDAO.addUserLogin(user.getUserLogin());
			} catch(Exception e) {
				logger.error("Error insertin user into Redis", e);
			}
			return true;
		}
	}

	@Override
	public void updateUser(User user) {
		userDbDAO.updateUser(user);
		if(userRedisDAO.getUserById(user.getId()) != null) {
			userRedisDAO.updateUser(user);
		}
	}

	@Override
	public void deleteUser(int userId) {
		userDbDAO.deleteUser(userId);
		if(userRedisDAO.getUserById(userId) != null) {
			userRedisDAO.deleteUser(userId);
		}
	}

	@Override
	public UserLogin getUserLoginByLogin(String login) {
		UserLogin userLogin = null;
		try {
			userLogin = userLoginRedisDAO.getUserLoginByLogin(login); //attempt to get from Redis first
			if(userLogin == null) {
				userLogin = userLoginDbDAO.getUserLoginByLogin(login); //if not found, try db
				if(userLogin != null) {
					userLoginRedisDAO.addUserLogin(userLogin); //if found in db, add to Redis
				}
			}
		} catch(Exception e) { //fallback to db
			logger.error("Error feching user login from Redis, fallback to db...", e);
			userLogin = userLoginDbDAO.getUserLoginByLogin(login);
		}
		return userLogin;
	}

	@Override
	public void updateLogin(UserLogin userLogin) {
		userLoginDbDAO.updateLogin(userLogin);
		if(userLoginRedisDAO.userLoginExists(userLogin.getLogin())) {
			userLoginRedisDAO.updateLogin(userLogin);
		}
	}

	@Override
	public boolean userLoginExists(String login) {
		return (userLoginRedisDAO.userLoginExists(login)) || userLoginDbDAO.userLoginExists(login);
	}

	@Override
	public boolean userExists(String email) {
		return (userRedisDAO.userExists(email)) || userDbDAO.userExists(email);
	}
}
