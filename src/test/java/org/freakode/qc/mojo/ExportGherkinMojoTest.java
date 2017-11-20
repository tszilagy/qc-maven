package org.freakode.qc.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class ExportGherkinMojoTest
{
	@Test
	public void exportTest() throws MojoExecutionException
	{
		ExportGherkinMojo exportGherkinMojo = new ExportGherkinMojo();
		exportGherkinMojo.setUri("http://qc/qcbin");
		exportGherkinMojo.setTestSetId("test-set-id");
		exportGherkinMojo.execute();
	}
}
