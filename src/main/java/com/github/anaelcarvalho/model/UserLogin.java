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
package com.github.anaelcarvalho.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * UserLogin entity.
 *
 * @author anaelcarvalho
 */
@Entity
@NamedQueries({
	@NamedQuery(name="User.findByLogin", query="SELECT ul FROM UserLogin ul WHERE ul.login = :login")
})
@Table(indexes = {@Index(name = "user_login",  columnList="login", unique = true)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserLogin implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5661681781788922801L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false, updatable=false, insertable=true)
	@JsonIgnore
	private User user;

	@Column(nullable=false, unique=true)
	@NotNull
	private String login;

	@Column(nullable=false)
	@NotNull
	private String password;

	@Column(unique=true)
	private Integer userIdFK; //TODO: remove key duplication

	@Column(nullable=false)
	@NotNull
	private String role = "USER"; //default role //TODO: implement endpoint to change role

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getUserIdFK() {
		return userIdFK;
	}

	public void setUserIdFK(Integer userIdFK) {
		this.userIdFK = userIdFK;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
