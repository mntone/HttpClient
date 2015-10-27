package net.mntone.httpclient.demo;

import net.mntone.httpclient.FormUrlEncodedContent;
import net.mntone.httpclient.HttpClient;
import net.mntone.httpclient.HttpContent;
import net.mntone.httpclient.HttpResponseMessage;
import net.mntone.httpclient.headers.HttpContentHeaders;
import net.mntone.httpclient.headers.HttpHeaderValueCollection;
import net.mntone.httpclient.headers.HttpResponseHeaders;
import net.mntone.httpclient.headers.MediaTypeHeaderValue;

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
			final HttpContent content = response.getContent();
			final HttpContentHeaders contentHeaders = content.getHeaders();
			final MediaTypeHeaderValue contentType = contentHeaders.getContentType();
			final Long contentLength = contentHeaders.getContentLength();
			final String resText = content.readAsStringAsync().get();
			System.out.print(resText);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}