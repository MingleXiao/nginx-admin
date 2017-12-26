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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.jboss.vfs.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jslsolucoes.nginx.admin.error.NginxAdminException;
import com.jslsolucoes.nginx.admin.i18n.Messages;
import com.jslsolucoes.nginx.admin.model.Nginx;
import com.jslsolucoes.nginx.admin.repository.NginxRepository;
import com.jslsolucoes.nginx.admin.template.TemplateProcessor;

@RequestScoped
public class NginxRepositoryImpl extends HibernateRepositoryImpl<Nginx> implements NginxRepository {

	private static Logger logger = LoggerFactory.getLogger(LogRepositoryImpl.class);

	public NginxRepositoryImpl() {
		// Default constructor
	}

	@Inject
	public NginxRepositoryImpl(Session session) {
		super(session);
	}

	@Override
	public Nginx configuration() {
		Criteria criteria = session.createCriteria(Nginx.class);
		return (Nginx) criteria.uniqueResult();
	}

	@Override
	public List<String> validateBeforeSaveOrUpdate(Nginx nginx) {
		List<String> errors = new ArrayList<>();

		if (!new File(nginx.getBin()).exists()) {
			errors.add(Messages.getString("nginx.invalid.bin.file", nginx.getBin()));
		}

		File settings = new File(nginx.getSettings());
		if (!canWriteOnFolder(settings)) {
			errors.add(Messages.getString("nginx.invalid.settings.permission", nginx.getSettings()));
		}
		return errors;
	}

	private boolean canWriteOnFolder(File settings) {
		if (!settings.exists()) {
			try {
				FileUtils.forceMkdir(settings);
				return true;
			} catch (IOException exception) {
				logger.error("Could not create on folder", exception);
				return false;
			}
		} else {
			try {
				FileUtils.touch(new File(settings, "touch.txt"));
				return true;
			} catch (IOException exception) {
				logger.error("Could not touch on folder", exception);
				return false;
			}
		}
	}

	@Override
	public OperationResult saveOrUpdateAndConfigure(Nginx nginx) throws NginxAdminException {
		nginx.setSettings(normalize(nginx.getSettings()));
		nginx.setBin(normalize(nginx.getBin()));
		configure(nginx);
		return super.saveOrUpdate(nginx);
	}

	private String normalize(String path) {
		return path.replaceAll("\\\\", "/");
	}

	private void configure(Nginx nginx) throws NginxAdminException {
		try {
			copy(nginx);
			conf(nginx);
			root(nginx);
		} catch (IOException | NginxAdminException e) {
			throw new NginxAdminException(e);
		}
	}

	private void root(Nginx nginx) throws NginxAdminException {
		TemplateProcessor.build().withTemplate("root.tpl").withData("nginx", nginx)
				.toLocation(new File(nginx.virtualHost(), "root.conf")).process();
	}

	private void conf(Nginx nginx) throws NginxAdminException {
		TemplateProcessor.build().withTemplate("nginx.tpl").withData("nginx", nginx)
				.toLocation(new File(nginx.setting(), "nginx.conf")).process();
	}

	private void copy(Nginx nginx) throws IOException {
		FileUtils.forceMkdir(nginx.setting());
		copyToDirectory(getClass().getResource("/template/fixed/nginx"), nginx.setting(),
				file -> !"tpl".equals(FilenameUtils.getExtension(file.getName())));
	}

	public void copyToDirectory(URL url, File destination, FileFilter fileFilter) throws IOException {
		if ("vfs".equals(url.getProtocol())) {
			copyFromVFS((VirtualFile) url.getContent(), destination, fileFilter);
		} else if ("jar".equals(url.getProtocol())) {
			copyFromJar(url, destination, fileFilter);
		} else {
			copyFromFile(url, destination, fileFilter);
		}
	}

	public void copyFromFile(URL url, File destination, FileFilter fileFilter) throws IOException {
		File source = new File(url.getPath());
		if (source.isDirectory()) {
			org.apache.commons.io.FileUtils.copyDirectory(source, destination, fileFilter);
		} else {
			org.apache.commons.io.FileUtils.copyFileToDirectory(source, destination);
		}
	}

	private void copyFromJar(URL url, File destination, FileFilter fileFilter) throws IOException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		Enumeration<JarEntry> files = jarURLConnection.getJarFile().entries();
		while (files.hasMoreElements()) {
			JarEntry entry = files.nextElement();
			if (!entry.isDirectory()) {
				File file = new File(destination, entry.getName());
				if (fileFilter.accept(file)) {
					org.apache.commons.io.FileUtils
							.copyInputStreamToFile(jarURLConnection.getJarFile().getInputStream(entry), file);
				}
			} else {
				org.apache.commons.io.FileUtils.forceMkdir(new File(destination, entry.getName()));
			}
		}
	}

	private static void copyFromVFS(VirtualFile virtualRootFile, File dest, FileFilter fileFilter) throws IOException {
		for (VirtualFile virtualFile : virtualRootFile.getChildren()) {
			String fileName = virtualFile.getName();
			if (!virtualFile.isDirectory()) {
				File file = new File(dest, fileName);
				if (fileFilter.accept(file)) {
					FileUtils.copyInputStreamToFile(virtualFile.openStream(), file);
				}
			} else {
				File created = new File(dest, fileName);
				FileUtils.forceMkdir(created);
				copyFromVFS(virtualFile, created, fileFilter);
			}
		}
	}
}
