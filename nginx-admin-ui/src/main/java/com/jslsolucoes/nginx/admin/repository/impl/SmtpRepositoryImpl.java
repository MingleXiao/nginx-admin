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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.jslsolucoes.nginx.admin.model.Smtp;
import com.jslsolucoes.nginx.admin.repository.SmtpRepository;

@RequestScoped
public class SmtpRepositoryImpl extends HibernateRepositoryImpl<Smtp> implements SmtpRepository {

	public SmtpRepositoryImpl() {
		// Default constructor
	}

	@Inject
	public SmtpRepositoryImpl(Session session) {
		super(session);
	}

	@Override
	public Smtp configuration() {
		Criteria criteria = session.createCriteria(Smtp.class);
		return (Smtp) criteria.uniqueResult();
	}
}
