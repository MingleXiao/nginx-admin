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
package com.jslsolucoes.nginx.admin.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.jslsolucoes.nginx.admin.model.VirtualHostAlias;

import net.sf.jasperreports.engine.JRException;

public interface ReportRepository {

	public List<String> validateBeforeSearch(List<VirtualHostAlias> aliases, LocalDate from, LocalTime fromTime,
			LocalDate to, LocalTime toTime);

	public InputStream statistics(List<VirtualHostAlias> aliases, LocalDate from, LocalTime fromTime, LocalDate to,
			LocalTime toTime) throws JRException, SQLException, IOException;

}
