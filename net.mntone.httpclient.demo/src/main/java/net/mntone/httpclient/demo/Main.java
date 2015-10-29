package net.mntone.httpclient.demo;

import net.mntone.httpclient.FormUrlEncodedContent;
import net.mntone.httpclient.HttpClient;
import net.mntone.httpclient.HttpContent;
import net.mntone.httpclient.HttpResponseMessage;
import net.mntone.httpclient.headers.HttpContentHeaders;
import net.mntone.httpclient.headers.HttpHeaderValueCollection;
import net.mntone.httpclient.headers.HttpResponseHeaders;
import net.mntone.httpclient.headers.MediaTypeHeaderValue;
import net.mntone.httpclient.headers.ProductInfoHeaderValue;

import java.util.Date;
import java.util.HashMap;

public class Main
{
	public static void main(final String[] args)
	{
		final HashMap<String, String> postContent = new HashMap<String, String>();
		postContent.put("test1", "test");
		postContent.put("test2", "test");
		final HttpClient client = new HttpClient();
		try
		{
			final HttpResponseMessage response = client.postAsync("http://httpbin.org/post", new FormUrlEncodedContent(postContent)).get();
			final HttpResponseHeaders responseHeaders = response.getHeaders();
			final HttpHeaderValueCollection<String> connection = responseHeaders.getConnection();
			final Date date = responseHeaders.getDate();
			final HttpHeaderValueCollection<ProductInfoHeaderValue> server = responseHeaders.getServer();
			final HttpContent content = response.getContent();
			final HttpContentHeaders contentHeaders = content.getHeaders();
			final MediaTypeHeaderValue contentType = contentHeaders.getContentType();
			final Long contentLength = contentHeaders.getContentLength();
			final String contentText = content.readAsStringAsync().get();

			final StringBuilder builder = new StringBuilder();
			builder.append(String.format("Connection: %s\n", connection));
			builder.append(String.format("Date: %s\n", date));
			builder.append(String.format("Server: %s\n", server));
			builder.append(String.format("Content-Type: %s\n", contentType));
			builder.append(String.format("Content-Length: %s\n", contentLength));
			builder.append('\n');
			builder.append(contentText);
			System.out.print(builder.toString());
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}