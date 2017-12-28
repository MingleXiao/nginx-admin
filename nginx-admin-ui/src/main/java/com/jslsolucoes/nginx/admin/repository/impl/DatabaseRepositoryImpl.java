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
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jslsolucoes.nginx.admin.annotation.Application;
import com.jslsolucoes.nginx.admin.error.NginxAdminRuntimeException;
import com.jslsolucoes.nginx.admin.repository.ConfigurationRepository;
import com.jslsolucoes.nginx.admin.repository.DatabaseRepository;

@RequestScoped
public class DatabaseRepositoryImpl implements DatabaseRepository {

	private EntityManager entityManager;
	private ConfigurationRepository configurationRepository;
	private Properties properties;
	private static Logger logger = LoggerFactory.getLogger(LogRepositoryImpl.class);

	public DatabaseRepositoryImpl() {
		// Default constructor
	}

	@Inject
	public DatabaseRepositoryImpl(@Application Properties properties, ConfigurationRepository configurationRepository,
			EntityManager entityManager) {
		this.properties = properties;
		this.configurationRepository = configurationRepository;
		this.entityManager = entityManager;
	}

	@Override
	public void installOrUpgrade() {
		AtomicInteger version = new AtomicInteger(installed());
		while (version.get() < actual()) {	
			Arrays.asList(resource("/sql/" + version.incrementAndGet() + ".sql").split(";")).stream()
					.filter(StringUtils::isNotEmpty).forEach(statement -> {
						try {
							entityManager.createNativeQuery(statement).executeUpdate();
						} catch (Exception e) {
							logger.error("Could not execute statement " + statement,e);
						}
					});
		}
	}

	private String resource(String path) {
		try {
			return IOUtils.toString(getClass().getResourceAsStream(path), "UTF-8");
		} catch (IOException e) {
			throw new NginxAdminRuntimeException(e);
		}
	}

	public Integer installed() {
		try {
			return configurationRepository.integer(ConfigurationType.DB_VERSION);
		} catch (Exception exception) {
			logger.warn("Database is not installed");
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
