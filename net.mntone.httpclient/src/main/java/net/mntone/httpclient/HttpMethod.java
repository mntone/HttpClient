package net.mntone.httpclient;

public final class HttpMethod
{
	public static final HttpMethod Get = new HttpMethod("GET", 0);
	public static final HttpMethod Post = new HttpMethod("POST", 0);
	public static final HttpMethod Head = new HttpMethod("HEAD", 0);
	public static final HttpMethod Put = new HttpMethod("PUT", 0);
	public static final HttpMethod Delete = new HttpMethod("DELETE", 0);
	public static final HttpMethod Options = new HttpMethod("OPTIONS", 0);
	public static final HttpMethod Trace = new HttpMethod("TRACE", 0);

	private final String _method;

	private HttpMethod(final String method, int dummy)
	{
		this._method = method;
	}

	public HttpMethod(final String method) throws IllegalAccessException
	{
		if (method == null)
		{
			throw new IllegalAccessException();
		}
		this._method = method;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof HttpMethod)
		{
			return this.equals((HttpMethod)other);
		}
		return false;
	}

	public boolean equals(final HttpMethod other)
	{
		return other != null && (this._method == other._method || this._method.equalsIgnoreCase(other._method));
	}

	@Override
	public int hashCode()
	{
		return this._method.toUpperCase().hashCode();
	}

	@Override
	public String toString()
	{
		return this._method;
	}
}