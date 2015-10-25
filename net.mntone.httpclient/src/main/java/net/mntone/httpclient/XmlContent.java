package net.mntone.httpclient;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class XmlContent extends ByteArrayContent
{
	private static TransformerFactory getTransformerFactory()
	{
		if (_transformerFactory == null)
		{
			_transformerFactory = TransformerFactory.newInstance();
		}
		return _transformerFactory;
	}
	private static TransformerFactory _transformerFactory;


	public XmlContent(final Document content) throws TransformerException
	{
		this(content, null);
	}

	public XmlContent(final Document content, final Charset charset) throws TransformerException
	{
		this(content, charset, null);
	}

	public XmlContent(final Document content, Charset charset, String mediaType) throws TransformerException
	{
		super(getByteArrayContent(content, charset));

		if (mediaType == null || mediaType.isEmpty()) mediaType = "application/xml";
		this.setContentType(mediaType, charset);
	}

	private static byte[] getByteArrayContent(final Document content, Charset charset) throws TransformerException
	{
		if (content == null) throw new IllegalArgumentException();
		if (charset == null) charset = DEFAULT_CHARSET;

		final Transformer transformer = getTransformerFactory().newTransformer();
		final Properties format = new Properties();
		format.setProperty(OutputKeys.INDENT, "no");
		format.setProperty(OutputKeys.METHOD, "xml");
		format.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		format.setProperty(OutputKeys.VERSION, "1.0");
		format.setProperty(OutputKeys.ENCODING, charset.name());
		transformer.setOutputProperties(format);

		final DOMSource xmlSource = new DOMSource(content.getDocumentElement());
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final StreamResult result = new StreamResult(output);
		transformer.transform(xmlSource, result);
		return output.toByteArray();
	}
}