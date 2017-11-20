package org.freakode.qc.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A utility class for converting between jaxb annotated objects and xml.
 */
public class EntityMarshallingUtils
{

	private static final Logger LOGGER = Logger.getLogger(EntityMarshallingUtils.class.getName());

	private EntityMarshallingUtils()
	{
	}

	/**
	 * @param <T>
	 *            the type we want to convert our xml into
	 * @param c
	 *            the class of the parameterized type
	 * @param xml
	 *            the instance xml description
	 * @return a deserialization of the xml into an object of type T
	 *           of class Class<T>
	 * @throws javax.xml.bind.JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T marshal(Class<T> c, String xml) throws JAXBException
	{
		T res;
		if (c == xml.getClass())
		{
			res = (T) xml;
		}
		else
		{
			String packageName = c.getPackage().getName();
			JAXBContext ctx = JAXBContext.newInstance(packageName);
			Unmarshaller marshaller = ctx.createUnmarshaller();
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			//Create an XMLReader to use with our filter
			XMLReader reader = null;
			try
			{
				reader = XMLReaderFactory.createXMLReader();
			}
			catch (SAXException e)
			{
				e.printStackTrace();
			}
			String namespace = c.getAnnotation(XmlRootElement.class).namespace();
			NamespaceFilter inFilter = new NamespaceFilter(namespace.intern(), true);
			inFilter.setParent(reader);
			InputSource is = new InputSource(inputStream);
			SAXSource source = new SAXSource(inFilter, is);

			res = (T) marshaller.unmarshal(source);
		}
		return res;
	}

	/**
	 * @param <T>
	 *            the type to serialize
	 * @param c
	 *            the class of the type to serialize
	 * @param o
	 *            the instance containing the data to serialize
	 * @return a string representation of the data.
	 * @throws JAXBException 
	 * @throws FactoryConfigurationError 
	 * @throws XMLStreamException 
	 * @throws Exception
	 */
	public static <T> String unmarshal(Object o) throws JAXBException, XMLStreamException, FactoryConfigurationError
	{

		JAXBContext ctx = JAXBContext.newInstance(o.getClass());
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter entityXml = new StringWriter();
		XMLStreamWriter writer;
		writer = XMLOutputFactory.newInstance().createXMLStreamWriter(entityXml);
		writer.setNamespaceContext(new NamespaceContext() {
			@Override
			public Iterator<String> getPrefixes(String namespaceURI)
			{
				return null;
			}

			@Override
			public String getPrefix(String namespaceURI)
			{
				return "";
			}

			@Override
			public String getNamespaceURI(String prefix)
			{
				return "";
			}
		});

		marshaller.marshal(o, writer);

		String entityString = entityXml.toString();

		return entityString;
	}

}