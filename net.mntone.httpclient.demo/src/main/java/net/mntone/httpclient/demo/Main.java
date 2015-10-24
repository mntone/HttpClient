package net.mntone.httpclient.demo;

import net.mntone.httpclient.HttpClient;

public class Main
{
	public static void main(final String[] args)
	{
		final HttpClient client = new HttpClient();
		try
		{
			final String res = client.getStringAsync("http://mntone.minibird.jp/").get();
			System.out.print(res);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}