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
package com.jslsolucoes.nginx.admin.agent.runner.impl;

import com.jslsolucoes.nginx.admin.agent.i18n.Messages;
import com.jslsolucoes.nginx.admin.agent.os.OperationalSystemType;
import com.jslsolucoes.nginx.admin.agent.runner.Runner;
import com.jslsolucoes.nginx.admin.agent.runner.RunnerType;
import com.jslsolucoes.nginx.admin.agent.runtime.RuntimeResult;
import com.jslsolucoes.nginx.admin.agent.runtime.RuntimeResultType;

@RunnerType(OperationalSystemType.UNKNOW)
public class UnknowRunner implements Runner {

	private static final RuntimeResult RUNTIME_RESULT = new RuntimeResult(RuntimeResultType.ERROR,
			Messages.getString("machine.runner.not.found"));

	@Override
	public String start(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String version(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stop(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String restart(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String status(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String reload(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testConfig(String bin, String conf) {
		// TODO Auto-generated method stub
		return null;
	}


}
