package org.freakode.qc.infrastructure.entities;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.freakode.qc.Entities;
import org.freakode.qc.infrastructure.EntityMarshallingUtils;
import org.freakode.qc.infrastructure.Response;
import org.freakode.qc.infrastructure.RestConnector;

public class TestInstanceEntitiesConnector
{
	private static TestInstanceEntitiesConnector instance = new TestInstanceEntitiesConnector();
	private RestConnector restConnector = RestConnector.getInstance();

	private TestInstanceEntitiesConnector()
	{
	}

	public static TestInstanceEntitiesConnector getInstance()
	{
		return instance;
	}

	public Entities getEntities(String id) throws JAXBException, IOException
	{
		Entities entities = null;
		String url = restConnector.buildEntityCollectionUrl("test-instance") + "?query=%7Bcycle-id%5B" + id + "%5D%7D";
		Response serverResponse = restConnector.httpGet(url, null, RequestHeaders.getRequestHeaders());
		entities = EntityMarshallingUtils.marshal(Entities.class, serverResponse.toString());
		return entities;
	}

}
