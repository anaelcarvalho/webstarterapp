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

import com.github.anaelcarvalho.model.User;

/**
 * User Data Access Object interface, defining available operations
 *
 * @author anaelcarvalho
 */
public interface UserDAO {
	/**
	 * Returns the User entity with the provided id.
	 *
	 * @param      userId   the id of the user to return.
	 * @return     the User entity with the provided id or null if no User was found.
	 */
	public User getUserById(int userId);
	/**
	 * Persists a new User entity.
	 *
	 * @param      user   the User entity to persist.
	 * @return     the generated id of the persisted User entity.
	 */
	public Integer addUser(User user);
	/**
	 * Updates an existing User entity.
	 *
	 * @param      user   the User entity to update.
	 */
	public void updateUser(User user);
	/**
	 * Deletes an existing User entity.
	 *
	 * @param      userId   the id of the user to delete.
	 */
	public void deleteUser(int userId);
	/**
	 * Verifies an User entity exists with the provided attribute (email).
	 *
	 * @param      email   the email String attribute to verify.
	 * @return     the flag indicating whether a User was found with the provided attribute.
	 */
	public boolean userExists(String email);
}
