package net.mntone.httpclient;

public final class HttpRequestException extends Exception
{
	public HttpRequestException()
	{
		super();
	}

	public HttpRequestException(final String message)
	{
		super(message);
	}

	public HttpRequestException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}