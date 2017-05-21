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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.github.anaelcarvalho.dao.UserDAO;
import com.github.anaelcarvalho.model.User;

/**
 * Implementation of UserDAO interface for use with relational databases.
 *
 * @author anaelcarvalho
 * @see     com.github.anaelcarvalho.dao.UserDAO
 */
@Transactional
@Repository
@Component("user-db")
public class UserDAOImpl implements UserDAO {
	@PersistenceContext	
	private EntityManager entityManager;	

	@Override
	public User getUserById(int userId) {
		return entityManager.find(User.class, userId);
	}

	@Override
	public Integer addUser(User user) {
		entityManager.persist(user);
		entityManager.flush();
		return user.getId();
	}

	@Override
	public void updateUser(User user) {
		User dbUser = getUserById(user.getId());
		dbUser.setFirstName(user.getFirstName());
		dbUser.setLastName(user.getLastName());
		dbUser.setEmail(user.getEmail());
		entityManager.flush();
	}

	@Override
	public void deleteUser(int userId) {
		entityManager.remove(getUserById(userId));
	}

	@Override
	public boolean userExists(String email) {
		return !entityManager.createNamedQuery("User.findByEmail").setParameter("email", email).getResultList().isEmpty();
	}
}
