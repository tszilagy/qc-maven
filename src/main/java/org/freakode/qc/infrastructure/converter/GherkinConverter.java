package org.freakode.qc.infrastructure.converter;

import java.util.List;

import org.freakode.qc.Entity;
import org.freakode.qc.FieldComplexType;

public class GherkinConverter
{
	private static GherkinConverter instance = new GherkinConverter();

	private GherkinConverter()
	{
	}

	public static GherkinConverter getInstance()
	{
		return instance;
	}

	public String convert(List<Entity> entities)
	{
		StringBuilder scenario = new StringBuilder("Scenario: \n\nGiven ");
		for (Entity entity : entities)
		{
			String description = "";
			String expectation = "";
			for (FieldComplexType field : entity.getFields().getField())
			{
				for (FieldComplexType.Value value : field.getValue())
				{
					String plainValue = getPlainValue(value);
					if ("description".equals(field.getName()))
					{
						description += plainValue;
					}
					else if ("expected".equals(field.getName()))
					{
						expectation += plainValue;
					}
				}
			}
			if (expectation.isEmpty())
			{
				scenario.append(description).append("\n");
			}
			else
			{
				scenario.append("When ").append(description).append("\n");
				scenario.append("Then ").append(expectation);
			}
		}
		return scenario.toString();
	}

	private String getPlainValue(FieldComplexType.Value value)
	{
		String plainValue = value.getValue().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "");
		return plainValue;
	}
}
