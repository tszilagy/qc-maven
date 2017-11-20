package org.freakode.qc.mojo;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.freakode.qc.Entity;
import org.freakode.qc.FieldComplexType;
import org.freakode.qc.infrastructure.Authenticate;
import org.freakode.qc.infrastructure.RestConnector;
import org.freakode.qc.infrastructure.converter.GherkinConverter;
import org.freakode.qc.infrastructure.entities.RunStepEntitiesConnector;
import org.freakode.qc.infrastructure.entities.RunTestInstancesConnector;
import org.freakode.qc.infrastructure.entities.TestInstanceEntitiesConnector;


/**
 * Says "Hi" to the user.
 *
 */
@Mojo(name = "export-qc-gherkin")
public class ExportGherkinMojo extends AbstractMojo
{
	@Parameter(property = "uri")
	private String uri;

	@Parameter(property = "username")
	private String username;

	@Parameter(property = "password")
	private String password;

	@Parameter(property = "domain")
	private String domain;

	@Parameter(property = "project")
	private String project;

	@Parameter(property = "test-set-id")
	private String testSetId;

	private static final Logger LOGGER = Logger.getLogger(ExportGherkinMojo.class.getName());
	private RunStepEntitiesConnector runStepConnector = RunStepEntitiesConnector.getInstance();
	private TestInstanceEntitiesConnector testInstanceConnector = TestInstanceEntitiesConnector.getInstance();
	private RunTestInstancesConnector runTestInstancesConnector = RunTestInstancesConnector.getInstance();
	private GherkinConverter gherkinConverter = GherkinConverter.getInstance();


	public void execute()
	{
		RestConnector con = RestConnector.getInstance().init(new HashMap<String, String>(), uri, domain, project);

		Authenticate session = new Authenticate();
		try
		{
			boolean loginResponse = session.login(username, password);
			if (loginResponse)
			{
			con.getQCSession();
			Entity entity = runTestInstancesConnector.getEntities(testInstanceConnector.getEntities("6608"), "test-run", username, "Blocked").getEntity().get(0);
			String runId = null;
			for (FieldComplexType field : entity.getFields().getField())
			{
				if ("id".equals(field.getName()))
				{
					runId = field.getValue().get(0).getValue();
				}
			}
			LOGGER.info(gherkinConverter.convert(runStepConnector.getEntities(runId).getEntity()));
			
			session.logout();
			}
			else
			{
				LOGGER.log(Level.SEVERE, "Unable to log in.");
			}
		}
		catch (JAXBException | FactoryConfigurationError | IOException e)
		{
			LOGGER.log(Level.SEVERE, "Exception thrown!", e);
		}
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public void setProject(String project)
	{
		this.project = project;
	}

	public void setTestSetId(String testSetId)
	{
		this.testSetId = testSetId;
	}
}