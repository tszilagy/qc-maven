package org.freakode.qc.infrastructure.entities;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders
{

	private static final Map<String, String> requestHeaders = new HashMap<>();
	static
	{
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
	}

	public static Map<String, String> getRequestHeaders()
	{
		return new HashMap<>(requestHeaders);
	}
}
