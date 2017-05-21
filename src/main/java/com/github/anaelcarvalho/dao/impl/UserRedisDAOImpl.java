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
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.github.anaelcarvalho.dao.UserDAO;
import com.github.anaelcarvalho.model.User;

/**
 * Implementation of UserDAO interface for use with Redis.
 *
 * @author anaelcarvalho
 * @see     com.github.anaelcarvalho.dao.UserDAO
 */
@Repository
@Component("user-redis")
public class UserRedisDAOImpl implements UserDAO {
	private static final String KEY = "User";
	private static final String SET_KEY = "Email";

	@Autowired
	private RedisTemplate<String, Object> redisHashTemplate;
	private HashOperations<String, Integer, User> hashOps;
	private SetOperations<String, Object> setOps;

	@PostConstruct
	private void init() {
		hashOps = redisHashTemplate.opsForHash();
		setOps = redisHashTemplate.opsForSet();
	}

	@Override
	public User getUserById(int userId) {
		return (User) hashOps.get(KEY, userId);
	}

	@Override
	public Integer addUser(User user) {
		if(user.getId() == null) {
			user.setId((new Random()).nextInt(10));
		}
		hashOps.put(KEY, user.getId(), user);
		setOps.add(SET_KEY, user.getEmail());
		return user.getId();
	}

	@Override
	public void updateUser(User user) {
		hashOps.put(KEY, user.getId(), user);
		setOps.add(SET_KEY, user.getEmail());
	}

	@Override
	public void deleteUser(int userId) {
		User user = (User) hashOps.get(KEY, userId);
		hashOps.delete(KEY, userId);
		setOps.remove(SET_KEY, user.getEmail());
	}

	@Override
	public boolean userExists(String email) {
		return setOps.isMember(SET_KEY, email);
	}
}
