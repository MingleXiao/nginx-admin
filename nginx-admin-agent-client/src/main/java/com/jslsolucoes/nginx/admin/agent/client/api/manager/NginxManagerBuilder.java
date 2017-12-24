package com.jslsolucoes.nginx.admin.agent.client.api.manager;

import java.util.concurrent.ScheduledExecutorService;

import com.jslsolucoes.nginx.admin.agent.client.api.NginxAgentClientApiBuilder;

public class NginxManagerBuilder implements NginxAgentClientApiBuilder {

	private ScheduledExecutorService scheduledExecutorService;

	@Override
	public NginxManager build() {
		return new NginxManager(scheduledExecutorService);
	}

	public static NginxManagerBuilder newBuilder() {
		return new NginxManagerBuilder();
	}

	public NginxManagerBuilder withScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
		return this;
	}

}
