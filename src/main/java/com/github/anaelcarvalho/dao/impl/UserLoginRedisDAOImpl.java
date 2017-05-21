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
package com.github.anaelcarvalho.dao.impl;

import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.github.anaelcarvalho.dao.UserLoginDAO;
import com.github.anaelcarvalho.model.UserLogin;

/**
 * Implementation of UserLoginDAO interface for use with Redis.
 *
 * @author anaelcarvalho
 * @see     com.github.anaelcarvalho.dao.UserLoginDAO
 */
@Repository
@Component("login-redis")
public class UserLoginRedisDAOImpl implements UserLoginDAO {
	private static final String KEY = "UserLogin";
    
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, UserLogin> hashOps;

	@PostConstruct
	private void init() {
		hashOps = redisTemplate.opsForHash();
	}

	@Override
	public void addUserLogin(UserLogin userLogin) {
		if(userLogin.getId() == null) {
			userLogin.setId((new Random()).nextInt(10));
		}
		if(userLogin.getUser() != null) {
			userLogin.setUser(null);
		}
		hashOps.put(KEY, userLogin.getLogin(), userLogin);
	}

	@Override
	public UserLogin getUserLoginByLogin(String login) {
		return (UserLogin) hashOps.get(KEY, login);
	}

	@Override
	public void updateLogin(UserLogin userLogin) {
		hashOps.put(KEY, userLogin.getLogin(), userLogin);
	}

	@Override
	public boolean userLoginExists(String login) {
		return hashOps.hasKey(KEY, login);
	}
}
