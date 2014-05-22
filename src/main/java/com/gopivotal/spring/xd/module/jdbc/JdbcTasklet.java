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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author Dave Syer,
 * @author Christian Tzolov
 * 
 */
public class JdbcTasklet implements Tasklet, InitializingBean {

	private static final Log logger = LogFactory.getLog(JdbcTasklet.class);

	private NamedParameterJdbcTemplate jdbcTemplate;

	private String sql;

	private Collection<Resource> scripts;

	/**
	 * An SQL query to execute in the tasklet. The query can be a select, update, delete or insert, and it can contain
	 * embedded query parameters using a {@link BeanPropertySqlParameterSource} whose root context is the step context.
	 * So for example
	 * 
	 * <pre>
	 * DELETE from LEAD_INPUTS where ID=:jobParameters[idToDelete]
	 * </pre>
	 * 
	 * Note that the syntax for the named parameters is different from and not as flexible as Spring EL. So it might be
	 * better anyway if possible to use late binding to push step context properties into the query, e.g. this will work
	 * in a bean definition which is step scoped:
	 * 
	 * <pre>
	 * &lt;bean id="tasklet" class="org...JdbcTasklet" scope="step"&gt;
	 *   &lt;property name="sql"&gt;
	 *     &lt;value&gt;
	 * DELETE from LEAD_INPUTS where ID=#{jobParameters['i.to.delete']?:-1}
	 *     &lt;/value&gt;
	 *   &lt;/property&gt;
	 * &lt;/bean&gt;
	 * </pre>
	 * 
	 * @see BeanPropertySqlParameterSource
	 * 
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @param scripts
	 *            List of sql script urls. Accepts file:/ and classpath:/ resource types.
	 */
	public void setScript(Collection<Resource> scripts) {
		this.scripts = scripts;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void afterPropertiesSet() throws Exception {
		Assert.state(jdbcTemplate != null, "A DataSource must be provided");
		Assert.state((sql != null) || (scripts != null), "A SQL query must be provided");
	}

	/**
	 * Execute the {@link #setSql(String) SQL query} provided. If the query starts with "select" (case insensitive) the
	 * result is a list of maps, which is logged and added to the step execution exit status. Otherwise the query is
	 * executed and the result is an indication, also in the exit status, of the number of rows updated.
	 */
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
		ExitStatus exitStatus = stepExecution.getExitStatus();

		String msg = "";
		if (StringUtils.hasText(sql)) {
			msg = runCommand(chunkContext.getStepContext(), sql);
		} else if (!CollectionUtils.isEmpty(scripts)) {
			msg = runScripts(chunkContext, scripts, null);
		}

		stepExecution.setExitStatus(exitStatus.addExitDescription(msg));
		return RepeatStatus.FINISHED;
	}

	private String runScripts(ChunkContext chunkContext, Iterable<Resource> scripts, String encoding) throws Exception {

		Assert.notNull(chunkContext, "a valid instance is required");

		StringBuffer messages = new StringBuffer();

		if (scripts != null) {
			for (Resource resource : scripts) {
				String sqlCommand = scriptToString(resource, encoding);
				String msg = runCommand(chunkContext.getStepContext(), sqlCommand);
				messages.append(msg).append("\n");
			}
		}

		return messages.toString();
	}

	private String scriptToString(Resource resource, String encoding) throws Exception {

		InputStream stream = resource.getInputStream();

		BufferedReader reader = new BufferedReader((StringUtils.hasText(encoding) ? new InputStreamReader(stream,
				encoding) : new InputStreamReader(stream)));

		String message = "";

		try {
			message = IOUtils.toString(reader);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return message;
	}

	private String runCommand(StepContext steoContext, String sqlCommand) throws Exception {

		String msg = "";
		if (sqlCommand.trim().toUpperCase().startsWith("SELECT")) {
			logger.debug("Executing: " + sqlCommand);
			List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlCommand,
					new BeanPropertySqlParameterSource(steoContext));
			msg = "Result: " + result;
			logger.debug(msg);
		} else {
			logger.debug("Updating : " + sqlCommand);
			int updated = jdbcTemplate.update(sqlCommand, new BeanPropertySqlParameterSource(steoContext));
			msg = "Updated: " + updated + " rows";
			logger.debug(msg);
		}

		return msg;
	}
}
