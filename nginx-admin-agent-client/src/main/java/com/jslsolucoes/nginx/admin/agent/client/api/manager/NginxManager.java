package com.jslsolucoes.nginx.admin.agent.client.api.manager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

import com.jslsolucoes.nginx.admin.agent.client.api.NginxAgentClientApi;

public class NginxManager implements NginxAgentClientApi {
	
	private final ScheduledExecutorService scheduledExecutorService;

	public NginxManager(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}
	
	public CompletableFuture<Void> start() {
		return CompletableFuture.supplyAsync(() -> {
			/*
			try (RestClient restClient = RestClient.build()) {
				NginxStartRequest notificationRequest = new NginxStartRequest(subject, text,);
				Entity<NginxStartRequest> entity = Entity.entity(notificationRequest, MediaType.APPLICATION_JSON);
				WebTarget webTarget = restClient.target(NotificationRest.API_ENDPOINT);
				return webTarget.path(root()).request().post(entity, NotificationResponse.class);
			}
			*/
			return null;
		}, scheduledExecutorService);
	}

}
