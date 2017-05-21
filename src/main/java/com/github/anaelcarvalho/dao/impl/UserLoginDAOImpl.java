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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.github.anaelcarvalho.dao.UserLoginDAO;
import com.github.anaelcarvalho.model.UserLogin;

/**
 * Implementation of UserLoginDAO interface for use with relational databases.
 *
 * @author anaelcarvalho
 * @see     com.github.anaelcarvalho.dao.UserLoginDAO
 */
@Transactional
@Repository
@Component("login-db")
public class UserLoginDAOImpl implements UserLoginDAO {
	@PersistenceContext	
	private EntityManager entityManager;

	@Override
	public void addUserLogin(UserLogin userLogin) {
		entityManager.persist(userLogin);
	}

	@Override
	public UserLogin getUserLoginByLogin(String login) {
		List<?> results = entityManager.createNamedQuery("User.findByLogin").setParameter("login", login).getResultList();
		UserLogin foundEntity = null;
		if(!results.isEmpty()){
			foundEntity = (UserLogin) results.get(0);
		}
		return foundEntity;
	}

	@Override
	public void updateLogin(UserLogin userLogin) {
		UserLogin dbUser = getUserLoginByLogin(userLogin.getLogin());
		if(dbUser != null) {
			dbUser.setLogin(userLogin.getLogin());
			dbUser.setPassword(userLogin.getPassword());
			entityManager.flush();
		}
	}

	@Override
	public boolean userLoginExists(String login) {
		return !entityManager.createNamedQuery("User.findByLogin").setParameter("login", login).getResultList().isEmpty();
	}
}
