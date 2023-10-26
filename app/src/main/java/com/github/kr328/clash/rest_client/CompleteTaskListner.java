package com.github.kr328.clash.rest_client;

import org.json.JSONObject;

public interface CompleteTaskListner
{
	public void completeTask(JSONObject result, int response_code);
}
