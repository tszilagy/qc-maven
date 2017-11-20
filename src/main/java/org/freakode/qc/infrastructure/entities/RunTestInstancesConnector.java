package org.freakode.qc.infrastructure.entities;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.freakode.qc.Entities;
import org.freakode.qc.Entity;
import org.freakode.qc.FieldComplexType;
import org.freakode.qc.FieldComplexType.Value;
import org.freakode.qc.FieldsComplexType;
import org.freakode.qc.infrastructure.EntityMarshallingUtils;
import org.freakode.qc.infrastructure.Response;
import org.freakode.qc.infrastructure.RestConnector;
import org.freakode.qc.mojo.ExportGherkinMojo;

public class RunTestInstancesConnector
{
	private static final Logger LOGGER = Logger.getLogger(ExportGherkinMojo.class.getName());
	private static RunTestInstancesConnector instance = new RunTestInstancesConnector();
	private RestConnector restConnector = RestConnector.getInstance();

	private RunTestInstancesConnector()
	{
	}

	public static RunTestInstancesConnector getInstance()
	{
		return instance;
	}

	public Entities getEntities(Entities request, String runName, String userName, String status) throws JAXBException, IOException
	{
		Entities entities = new Entities();
		String url = restConnector.buildEntityCollectionUrl("run");
		Entity runInstance = new Entity();
		runInstance.setType("run");
		runInstance.setFields(new FieldsComplexType());
		addNewField(runInstance, "name", runName);
		for (Entity testInstance : request.getEntity())
		{
			for (FieldComplexType field : testInstance.getFields().getField())
			{
				if ("test-config-id".equals(field.getName()))
				{
					addNewField(runInstance, "test-config-id", field.getValue().get(0).getValue());
				}
				else if ("cycle-id".equals(field.getName()))
				{
					addNewField(runInstance, "cycle-id", field.getValue().get(0).getValue());
				}
				else if ("test-id".equals(field.getName()))
				{
					addNewField(runInstance, "test-id", field.getValue().get(0).getValue());
				}
				else if ("id".equals(field.getName()))
				{
					addNewField(runInstance, "testcycl-id", field.getValue().get(0).getValue());
				}
			}
		}
		addNewField(runInstance, "owner", userName);
		addNewField(runInstance, "subtype-id", "hp.qc.run.manual");
		addNewField(runInstance, "status", status);

		String runs;
		try
		{
			runs = EntityMarshallingUtils.unmarshal(runInstance);
			Response serverResponse = restConnector.httpPost(url, runs.getBytes(), RequestHeaders.getRequestHeaders());
			Entity entity = EntityMarshallingUtils.marshal(Entity.class, serverResponse.toString());
			entities.getEntity().add(entity);
		}
		catch (XMLStreamException | FactoryConfigurationError e)
		{
			LOGGER.log(Level.SEVERE, "Exception thrown while posting run commands!", e);
		}
		return entities;
	}

	private void addNewField(Entity runInstance, final String fieldName, final String fieldValue)
	{
		FieldComplexType field = new FieldComplexType();
		field.setName(fieldName);
		Value value = new Value();
		value.setValue(fieldValue);
		field.getValue().add(value);
		runInstance.getFields().getField().add(field);
	}
}
