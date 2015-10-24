package net.mntone.httpclient;

final class HttpStatusDescription
{
	private static final String[][] _statusDescriptions = new String[][] {
		null, { "Continue", "Switching Protocols", "Processing" }, {
		"OK",
		"Created",
		"Accepted",
		"Non-Authoritative Information",
		"No Content",
		"Reset Content",
		"Partial Content",
		"Multi-Status",
		"Already Reported",
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		"IM Used"
	}, {
			"Multiple Choices",
			"Moved Permanently",
			"Found",
			"See Other",
			"Not Modified",
			"Use Proxy",
			"Switch Proxy",
			"Temporary Redirect",
			"Temporary Redirect",
			"Permanent Redirect"
		}, {
			"Bad Request",
			"Unauthorized",
			"Payment Required",
			"Forbidden",
			"Not Found",
			"Method Not Allowed",
			"Not Acceptable",
			"Proxy Authentication Required",
			"Request Timeout",
			"Conflict",
			"Gone",
			"Length Required",
			"Precondition Failed",
			"Payload Too Large",
			"Request-URI Too Long",
			"Unsupported Media Type",
			"Requested Range Not Satisfiable",
			"Expectation Failed",
			"I'm a teapot",
			null,
			null,
			"Misdirected Request",
			"Unprocessable Entity",
			"Locked",
			"Failed Dependency",
			null,
			"Upgrade Required",
			null,
			"Precondition Required",
			"Too Many Requests",
			null,
			"Request Header Fields Too Large"
		}, {
			"Internal Server Error",
			"Not Implemented",
			"Bad Gateway",
			"Service Unavailable",
			"Gateway Timeout",
			"Http Version Not Supported",
			"Variant Also Negotiates",
			"Insufficient Storage",
			"Loop Detected",
			null,
			"Not Extended",
			"Network Authentication Required"
		}
	};

	public static String getStatusDescription(final HttpStatusCode statusCode)
	{
		return getStatusDescription(statusCode.toInt());
	}

	public static String getStatusDescription(final int statusCode)
	{
		if (statusCode >= 100 && statusCode < 600)
		{
			final int hundred = statusCode / 100;
			final int one = statusCode % 100;
			if (one < _statusDescriptions[hundred].length)
			{
				return _statusDescriptions[hundred][one];
			}
		}
		return null;
	}
}