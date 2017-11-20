package org.freakode.qc.infrastructure.entities;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.freakode.qc.Entities;
import org.freakode.qc.infrastructure.EntityMarshallingUtils;
import org.freakode.qc.infrastructure.Response;
import org.freakode.qc.infrastructure.RestConnector;

public class RunStepEntitiesConnector
{
	private static RunStepEntitiesConnector instance = new RunStepEntitiesConnector();
	private RestConnector restConnector = RestConnector.getInstance();

	private RunStepEntitiesConnector()
	{
	}

	public static RunStepEntitiesConnector getInstance()
	{
		return instance;
	}

	public Entities getEntities(String id) throws JAXBException, IOException
	{
		Entities reply = null;
		String url = restConnector.buildEntityCollectionUrl("run-step") + "?query=%7Bparent-id%5B" + id + "%5D%7D";
		Response serverResponse = restConnector.httpGet(url, null, RequestHeaders.getRequestHeaders());
		reply = EntityMarshallingUtils.marshal(Entities.class, serverResponse.toString());
		return reply;
	}

}
