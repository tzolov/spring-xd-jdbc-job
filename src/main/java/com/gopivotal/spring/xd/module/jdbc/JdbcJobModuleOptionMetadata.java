/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gopivotal.spring.xd.module.jdbc;

import javax.validation.constraints.AssertTrue;

import org.springframework.util.StringUtils;
import org.springframework.xd.module.options.spi.ModuleOption;

public class JdbcJobModuleOptionMetadata {

	protected String driverClassName;

	protected String password;

	protected String url;

	protected String username;

	private String sql = "";

	private String script = "";
	
	private boolean restartable = false;

	@ModuleOption("the JDBC driver to use")
	public void setDriverClassName(String driverClass) {
		this.driverClassName = driverClass;
	}

	@ModuleOption("the JDBC password")
	public void setPassword(String password) {
		this.password = password;
	}

	@ModuleOption("the JDBC URL for the database")
	public void setUrl(String url) {
		this.url = url;
	}

	@ModuleOption("the JDBC username")
	public void setUsername(String username) {
		this.username = username;
	}

	@ModuleOption("the SQL to execute")
	public void setSql(String sql) {
		this.sql = sql;
	}

	@ModuleOption("the SQL script path to execute")
	public void setScript(String script) {
		this.script = script;
	}

	@AssertTrue(message = "Use 'sql' AND/OR 'script' to define the sql to be executed")
	boolean isEitherSqlOrScript() {
		return StringUtils.hasText(sql) || StringUtils.hasText(script);
	}
	
	@ModuleOption("Whether the job should be restartable or not in case of failure. Set this to false if the Job should not be restarted.")
	public void setRestartable(boolean restartable) {
		this.restartable = restartable;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getSql() {
		return sql;
	}

	public String getScript() {
		return script;
	}
	
	public boolean getRestartable() {
		return restartable;
	}
}
