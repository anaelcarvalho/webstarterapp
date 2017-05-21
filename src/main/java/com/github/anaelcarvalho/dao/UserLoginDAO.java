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
package com.github.anaelcarvalho.dao;

import com.github.anaelcarvalho.model.UserLogin;

/**
 * UserLogin Data Access Object interface, defining available operations
 *
 * @author anaelcarvalho
 */
public interface UserLoginDAO {
	/**
	 * Persists a new UserLogin entity.
	 *
	 * @param      userLogin   the UserLogin entity to persist.
	 */
	public void addUserLogin(UserLogin userLogin);
	/**
	 * Returns the UserLogin entity with the provided login.
	 *
	 * @param      login   the login of the UserLogin to return.
	 * @return     the UserLogin entity with the provided login or null if no UserLogin was found.
	 */
	public UserLogin getUserLoginByLogin(String login);
	/**
	 * Updates an existing UserLogin entity.
	 *
	 * @param      user   the UserLogin entity to update.
	 */
	public void updateLogin(UserLogin userLogin);
	/**
	 * Verifies an UserLogin entity exists with the provided attribute (login).
	 *
	 * @param      login   the login String attribute to verify.
	 * @return     the flag indicating whether a UserLogin was found with the provided attribute.
	 */
	public boolean userLoginExists(String login);
}
