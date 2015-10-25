package net.mntone.httpclient.demo;

import net.mntone.httpclient.FormUrlEncodedContent;
import net.mntone.httpclient.HttpClient;
import net.mntone.httpclient.HttpResponseMessage;

import java.util.HashMap;

public class Main
{
	public static void main(final String[] args)
	{
		final HashMap<String, String> content = new HashMap<String, String>();
		content.put("test1", "test");
		content.put("test2", "test");
		final HttpClient client = new HttpClient();
		try
		{
			//final String res = client.getStringAsync("http://mntone.minibird.jp/").get();
			final HttpResponseMessage res = client.postAsync("http://httpbin.org/post", new FormUrlEncodedContent(content)).get();
			final String resText = res.getContent().readAsStringAsync().get();
			System.out.print(resText);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}