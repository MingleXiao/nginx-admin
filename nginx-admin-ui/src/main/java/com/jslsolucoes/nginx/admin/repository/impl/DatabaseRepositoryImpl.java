/*******************************************************************************
 * Copyright 2016 JSL Solucoes LTDA - https://jslsolucoes.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.jslsolucoes.nginx.admin.repository.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jslsolucoes.nginx.admin.annotation.Application;
import com.jslsolucoes.nginx.admin.repository.ConfigurationRepository;
import com.jslsolucoes.nginx.admin.repository.DatabaseRepository;

@RequestScoped
public class DatabaseRepositoryImpl implements DatabaseRepository {

	@Resource(name="java:jboss/datasources/nginx-admin")
	private DataSource dataSource;
	private ConfigurationRepository configurationRepository;
	private Properties properties;
	private static Logger logger = LoggerFactory.getLogger(LogRepositoryImpl.class);

	public DatabaseRepositoryImpl() {
		// Default constructor
	}

	@Inject
	public DatabaseRepositoryImpl(@Application Properties properties,
			ConfigurationRepository configurationRepository) {
		this.properties = properties;
		this.configurationRepository = configurationRepository;
	}

	@Override
	public void installOrUpgrade() throws IOException {
		AtomicInteger version = new AtomicInteger(installed());
		while (version.get() < actual()) {
			Arrays.asList(resource("/sql/" + version.incrementAndGet() + ".sql").split(";")).stream()
					.filter(StringUtils::isNotEmpty).forEach(statement -> {
							try {
								Connection connection = dataSource.getConnection();
								try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
									preparedStatement.executeUpdate();
								}
							} catch (SQLException e) {
								logger.error("Could not execute statement",e);
							}
					});
		}
	}

	private String resource(String path) throws IOException {
		return IOUtils.toString(getClass().getResourceAsStream(path), "UTF-8");
	}

	public Integer installed() {
		try {
			return configurationRepository.integer(ConfigurationType.DB_VERSION);
		} catch (Exception exception) {
			logger.error("Database is not installed", exception);
			return 0;
		}
	}

	public Integer actual() {
		return Integer.valueOf(properties.getProperty("db.version"));
	}

	@Override
	public Boolean installOrUpgradeRequired() {
		return installed() != actual();
	}
}
