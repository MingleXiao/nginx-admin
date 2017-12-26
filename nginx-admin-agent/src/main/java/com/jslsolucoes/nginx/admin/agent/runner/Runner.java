package com.jslsolucoes.nginx.admin.agent.runner;

public interface Runner {

	public String start(String bin,String conf);

	public String version(String bin,String conf);

	public String stop(String bin,String conf);

	public String restart(String bin,String conf);

	public String status(String bin,String conf);

	public String reload(String bin,String conf);

	public String testConfig(String bin,String conf);
}
